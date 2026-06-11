package com.pers.service;

import com.pers.config.CurrencyProperties;
import com.pers.dto.response.CbrResponseDto;
import com.pers.dto.response.CurrencyRateResponseDto;
import com.pers.dto.response.CurrencyRatesResponseDto;
import com.pers.entity.ExchangeRate;
import com.pers.enums.Currency;
import com.pers.exception.CurrencyException;
import com.pers.exception.ErrorCode;
import com.pers.repository.ExchangeRateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;

import static com.pers.enums.Currency.RUB;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyService {

    private final ExchangeRateRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;
    private final CurrencyProperties currencyProperties;

    @Value("${value.exchangeRate.url}")
    private String API_URL;
    private static final String REDIS_PREFIX = "rate:";

    @Transactional
    public void updateRates() {
        LocalDate today = LocalDate.now();
        try {
            log.info("Запрос свежих курсов на {}", today);
            CbrResponseDto response = restTemplate.getForObject(API_URL, CbrResponseDto.class);
            if (response != null) {
                Map<String, BigDecimal> rates = response.getRates();

                currencyProperties.targetCurrencies().forEach(currency -> {
                    BigDecimal rateFromApi = rates.getOrDefault(currency.name(), BigDecimal.ZERO);

                    if (rateFromApi.compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal finalRate = BigDecimal.ONE.divide(rateFromApi, 2, RoundingMode.HALF_UP);
                        saveRate(currency, finalRate);
                    }
                });
                log.info("Курсы на {} успешно обновлены.", today);
            }
        } catch (Exception e) {
            log.error("Ошибка при получении курсов из API: {}. Используем fallback из БД.", e.getMessage());
            fallbackToDatabase();
        }
    }

    private void saveRate(Currency currency, BigDecimal rate) {
        repository.save(new ExchangeRate(currency, rate, LocalDate.now()));
        redisTemplate.opsForValue().set(REDIS_PREFIX + currency, rate.toString());
    }

    private void fallbackToDatabase() {
        currencyProperties.targetCurrencies().forEach(currency -> {
            repository.findById(currency).ifPresentOrElse(
                    record -> {
                        redisTemplate.opsForValue().set(REDIS_PREFIX + currency, record.getRate().toString());
                        log.info("Восстановлен курс из БД для {}: {}", currency, record.getRate());
                    },
                    () -> log.error("В БД нет данных для валюты {}", currency)
            );
        });
    }

    // Метод для получения курса другими сервисами (всегда из Redis)
    public BigDecimal getRateFromCache(Currency code) {
        if (code == RUB) {
            return BigDecimal.ONE;
        }
        String val = redisTemplate.opsForValue().get(REDIS_PREFIX + code);
        if (val != null) {
            return new BigDecimal(val);
        }
        return repository.findById(code)
                .map(ExchangeRate::getRate)
                .orElseThrow(() -> new CurrencyException(NOT_FOUND, ErrorCode.CURRENCY_RATE_NOT_FOUND, code));
    }

    public CurrencyRatesResponseDto getRates() {
        return new CurrencyRatesResponseDto(
                Stream.concat(
                                Stream.of(RUB),
                                currencyProperties.targetCurrencies().stream()
                        )
                        .distinct()
                        .map(currency -> new CurrencyRateResponseDto(
                                currency,
                                getRateFromCache(currency)
                        ))
                        .toList()
        );
    }

    public ConversionResult convert(BigDecimal amount, Currency source, Currency target) {
        BigDecimal sourceRate = getRateFromCache(source);
        BigDecimal targetRate = getRateFromCache(target);
        BigDecimal exchangeRate = sourceRate.divide(targetRate, 6, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
        return new ConversionResult(exchangeRate, convertedAmount);
    }

    public record ConversionResult(
            BigDecimal exchangeRate,
            BigDecimal convertedAmount
    ) {
    }
}
