package com.pers.service;

import com.pers.dto.request.CardRequestDto;
import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Card;
import com.pers.mapper.CardCreateMapper;
import com.pers.mapper.CardReadMapper;
import com.pers.repository.CardRepository;
import com.pers.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.pers.util.constant.Constants.CARD_NUMBER_LENGTH;
import static com.pers.util.constant.Constants.CURRENCY_PREFIXES;
import static com.pers.util.constant.Constants.DEFAULT_CARD_NUMBER_PREFIX;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardReadMapper cardReadMapper;
    private final CardCreateMapper cardCreateMapper;
    private final ClientRepository clientRepository;
    private final AccountService accountService;

    public Optional<CardResponseDto> findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    public Optional<CardResponseDto> findById(UUID id) {
        return cardRepository.findCardWithBalanceById(id);
    }


//    public Optional<CardResponseDto> updateStatusToBlocked(CardResponseDto cardDto) {
//        return Optional.of(cardDto)
//                .map(cardCreateMapper::mapStatusToBlocked)
//                .map(cardRepository::saveAndFlush)
//                .map(cardReadMapper::toDto);
//    }

    public void updateStatusToExpired(CardResponseDto cardDto) {
        Optional.of(cardDto)
                .map(cardCreateMapper::mapStatusExpired)
                .map(cardRepository::saveAndFlush);
    }

    public boolean delete(UUID id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    cardRepository.delete(entity);
                    cardRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public List<CardResponseDto> findByClientId(UUID clientId) {
        return cardRepository.findByClientId(clientId).stream()
                .map(card -> {
                    BigDecimal balance = accountService.getBalanceById(card.getAccountId());
                    return cardReadMapper.toDto(card, balance);
                })
                .toList();
    }

//    public List<CardResponseDto> findActiveCardsAndPositiveBalanceByClientId(UUID clientId) {
//        return cardRepository.findByClientId(clientId).stream()
//                .map(cardReadMapper::toDto)
//                .filter(dto -> dto.status() == Status.ACTIVE && dto.balance().compareTo(BigDecimal.ZERO) > 0)
//                .toList();
//    }

//    public Optional<CardResponseDto> findCardByClientPhone(String phone) {
//        Optional<Client> byPhone = Optional.of(clientRepository.findByPhone(phone)
//                .orElseThrow(() -> new RuntimeException("Клиент по такому номер не найден")));
//        return cardRepository.findByClientId(byPhone.get().getId()).stream()
//                .map(cardReadMapper::toDto)
//                .filter(card -> card.status() == Status.ACTIVE)
//                .findFirst();
//    }

//    public Page<CardResponseDto> findAllByFilter(CardFilterDto filter, Pageable pageable) {
//        return cardRepository.findAllByFilter(filter, pageable)
//                .map(cardReadMapper::toDto);
//    }

    //    public List<CardResponseDto> findActiveCardsByClientId(UUID clientId) {
//        return cardRepository.findByClientId(clientId).stream()
//                .map(cardReadMapper::toDto)
//                .filter(dto -> dto.status() == Status.ACTIVE)
//                .toList();
//    }
//
    public List<CardResponseDto> findByAccountId(UUID accountId) {
        return cardRepository.findByAccountId(accountId);
    }

//    @Scheduled(cron = "0 0 0 * * *")
//    public void checkCardExpire() {
//        cardRepository.findAll().stream()
//                .map(cardReadMapper::toDto)
//                .filter(card -> card.status() == Status.ACTIVE && card.expireDate().isBefore(LocalDate.now()))
//                .forEach(this::updateStatusToExpired);
//    }

//    public String createUniqueCardNumber() {
//        String cardNumber;
//        do {
//            cardNumber = generateCardNumber();
//        } while (cardRepository.existsByCardNumber(cardNumber));
//        return cardNumber;
//    }

//    private String generateCardNumber() {
//        ThreadLocalRandom random = ThreadLocalRandom.current();
//        StringBuilder sb = new StringBuilder(CARD_NUMBER_PREFIX);
//        for (int i = 0; i < CARD_NUMBER_LENGTH - CARD_NUMBER_PREFIX.length(); i++) {
//            sb.append(random.nextInt(10));
//        }
//        return sb.toString();
//    }

    @Transactional
    public CardResponseDto create(UUID clientId, CardRequestDto dto) {
        log.info("Создание новой карты c именем {} для клиента {}. Валюта: {}, Премиум: {}",
                dto.name(), clientId, dto.currency(), dto.isPremium());

        String prefix = CURRENCY_PREFIXES.getOrDefault(dto.currency(), DEFAULT_CARD_NUMBER_PREFIX);
        String cardNumber = generateUniqueCardNumber(prefix, dto.isPremium());
        Card card = cardCreateMapper.toEntity(dto, clientId, cardNumber);
        Card savedCard = cardRepository.save(card);
        return cardRepository.findByNumber(savedCard.getCardNumber()).orElseThrow();

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
        for (int i = 0; i < CARD_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(CARD_NUMBER_LENGTH));
        }
        return sb.toString();
    }

    private String generateBeautiful(String prefix) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int patternType = random.nextInt(3);

        return switch (patternType) {
            case 0 -> generateGoldenTail(prefix, random);     // Концовка 8888
            case 1 -> generateDoubleTriple(prefix, random);   // Концовка 111 222
            default -> generateRegular(prefix);
        };
    }

    // 6 случайных + 4 одинаковых (8888)
    private String generateGoldenTail(String prefix, ThreadLocalRandom random) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(CARD_NUMBER_LENGTH));
        }
        char digit = (char) (random.nextInt(CARD_NUMBER_LENGTH) + '0');
        sb.append(String.valueOf(digit).repeat(4));
        return sb.toString();
    }

    // 4 случайных + 3 одинаковых + 3 одинаковых (111222)
    private String generateDoubleTriple(String prefix, ThreadLocalRandom random) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(CARD_NUMBER_LENGTH));
        }
        int d1 = random.nextInt(CARD_NUMBER_LENGTH);
        int d2 = random.nextInt(CARD_NUMBER_LENGTH);
        sb.append(String.valueOf(d1).repeat(3));
        sb.append(String.valueOf(d2).repeat(3));
        return sb.toString();
    }
}



