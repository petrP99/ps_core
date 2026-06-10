package com.pers.service;

import com.pers.dto.response.CbrResponseDto;
import com.pers.dto.response.CurrencyRateResponseDto;
import com.pers.dto.response.CurrencyRatesResponseDto;
import com.pers.entity.ExchangeRate;
import com.pers.enums.Currency;
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
import java.util.List;
import java.util.Map;

import static com.pers.enums.Currency.CNY;
import static com.pers.enums.Currency.RUB;
import static com.pers.enums.Currency.USD;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyService {


    private final ExchangeRateRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;
    private final List<String> targetCurrencies = List.of(USD.name(), CNY.name()); // todo вынести в расширяемый из вне лист

    @Value("${value.exchangeRate.url}")
    private String API_URL;
    private static final String REDIS_PREFIX = "rate:";

    @Transactional
    public void updateRates() {
        try {
            log.info("Запрос свежих курсов...");
            CbrResponseDto response = restTemplate.getForObject(API_URL, CbrResponseDto.class);
            if (response != null) {
                Map<String, BigDecimal> rates = response.getRates();

                targetCurrencies.forEach(code -> {
                    BigDecimal rateFromApi = rates.get(code);

                    if (rateFromApi != null && rateFromApi.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal finalRate = BigDecimal.ONE.divide(rateFromApi, 2, RoundingMode.HALF_UP);
                        saveRate(code, finalRate);
                    }
                });
                log.info("Курсы успешно обновлены.");
            }
        } catch (Exception e) {
            log.error("Ошибка при получении курсов из API: {}. Используем fallback из БД.", e.getMessage());
            fallbackToDatabase();
        }
    }

    private void saveRate(String code, BigDecimal rate) {
        repository.save(new ExchangeRate(Currency.valueOf(code), rate, LocalDate.now()));
        redisTemplate.opsForValue().set(REDIS_PREFIX + code, rate.toString());
    }

    private void fallbackToDatabase() {
        targetCurrencies.forEach(code -> {
            repository.findById(Currency.valueOf(code)).ifPresentOrElse(
                    record -> {
                        redisTemplate.opsForValue().set(REDIS_PREFIX + code, record.getRate().toString());
                        log.info("Восстановлен курс из БД для {}: {}", code, record.getRate());
                    },
                    () -> log.error("В БД нет данных для валюты {}", code)
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
                .orElseThrow(() -> new IllegalStateException("Курс валюты " + code + " не найден"));
    }

    public CurrencyRatesResponseDto getRates() {
        return new CurrencyRatesResponseDto(
                List.of(
                        new CurrencyRateResponseDto(RUB, getRateFromCache(RUB)),
                        new CurrencyRateResponseDto(USD, getRateFromCache(USD)),
                        new CurrencyRateResponseDto(CNY, getRateFromCache(CNY))
                )
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
