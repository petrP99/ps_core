package com.pers.service;

import com.pers.dto.request.CardRequestDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Card;
import com.pers.enums.Status;
import com.pers.exception.CardException;
import com.pers.exception.ErrorCode;
import com.pers.mapper.CardMapper;
import com.pers.repository.AccountRepository;
import com.pers.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.pers.enums.Status.ACTIVE;
import static com.pers.util.constant.Constants.CARD_NUMBER_LENGTH;
import static com.pers.util.constant.Constants.CURRENCY_PREFIXES;
import static com.pers.util.constant.Constants.DEFAULT_CARD_NUMBER_PREFIX;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;

    public Optional<CardResponseDto> findById(UUID id) {
        return cardRepository.findCardWithBalanceById(id);
    }

    public List<CardResponseDto> findByClientId(UUID clientId) {
        return cardRepository.findCardsWithBalanceByClientId(clientId);
    }

    public List<CardResponseDto> findByAccountId(UUID accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    @Transactional
    public CardResponseDto create(UUID clientId, CardRequestDto dto) {
        log.info("Создание новой карты c именем {} для клиента {}. Валюта: {}, Премиум: {}",
                dto.name(), clientId, dto.currency(), dto.isPremium());

        var account = accountRepository.findByIdAndClientId(dto.accountId(), clientId)
                .orElseThrow(() -> new CardException(
                        NOT_FOUND,
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        dto.accountId()
                ));
        if (account.getStatus() != ACTIVE) {
            throw new CardException(CONFLICT, ErrorCode.CARD_CREATE_CLOSED_ACCOUNT);
        }
        if (account.getCurrency() != dto.currency()) {
            throw new CardException(BAD_REQUEST, ErrorCode.CARD_CURRENCY_MISMATCH);
        }

        String prefix = CURRENCY_PREFIXES.getOrDefault(account.getCurrency(), DEFAULT_CARD_NUMBER_PREFIX);
        String cardNumber = generateUniqueCardNumber(prefix, Boolean.TRUE.equals(dto.isPremium()));
        Card card = cardMapper.toEntity(dto, clientId, cardNumber);
        Card savedCard = cardRepository.save(card);
        return cardRepository.findByNumber(savedCard.getCardNumber())
                .orElseThrow(() -> new CardException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.CARD_NUMBER_NOT_FOUND,
                        savedCard.getCardNumber()
                ));

    }

    private String generateUniqueCardNumber(String prefix, boolean isPremium) {
        String number;
        do {
            number = isPremium ? generateBeautiful(prefix) : generateRegular(prefix);
        } while (cardRepository.existsByCardNumber(number));
        return number;
    }

    private String generateRegular(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = prefix.length(); i < CARD_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateBeautiful(String prefix) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextBoolean()
                ? generateGoldenTail(prefix, random)
                : generateDoubleTriple(prefix, random);
    }

    // Случайная часть + 4 одинаковых цифры в конце.
    private String generateGoldenTail(String prefix, ThreadLocalRandom random) {
        StringBuilder sb = new StringBuilder(prefix);
        int randomPartLength = CARD_NUMBER_LENGTH - prefix.length() - 4;
        for (int i = 0; i < randomPartLength; i++) {
            sb.append(random.nextInt(10));
        }
        char digit = (char) (random.nextInt(1, 10) + '0');
        sb.append(String.valueOf(digit).repeat(4));
        return sb.toString();
    }

    // Случайная часть + две разные группы по 3 одинаковых цифры.
    private String generateDoubleTriple(String prefix, ThreadLocalRandom random) {
        StringBuilder sb = new StringBuilder(prefix);
        int randomPartLength = CARD_NUMBER_LENGTH - prefix.length() - 6;
        for (int i = 0; i < randomPartLength; i++) {
            sb.append(random.nextInt(10));
        }
        int d1 = random.nextInt(10);
        int d2;
        do {
            d2 = random.nextInt(10);
        } while (d2 == d1);
        sb.append(String.valueOf(d1).repeat(3));
        sb.append(String.valueOf(d2).repeat(3));
        return sb.toString();
    }

    @Transactional
    public CardResponseDto blockById(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardException(NOT_FOUND, ErrorCode.CARD_NOT_FOUND, id));
        card.setStatus(Status.BLOCKED);
        cardRepository.flush();

        return cardRepository.findCardWithBalanceById(id)
                .orElseThrow(() -> new CardException(
                        INTERNAL_SERVER_ERROR,
                        ErrorCode.CARD_NOT_FOUND,
                        id
                ));
    }
}
