package com.pers.repository;

import com.pers.dto.response.CardResponseDto;
import com.pers.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CardRepository extends JpaRepository<Card, UUID>,
        QuerydslPredicateExecutor<Card> {

    List<Card> findAllByAccountId(UUID accountId);

    @Query("""
                SELECT new com.pers.dto.response.CardResponseDto(
                    c.id,
                    c.clientId,
                    c.accountId,
                    a.balance,
                    c.createdDate,
                    c.expireDate,
                    c.name,
                    c.currency,
                    c.status,
                    c.cardNumber
                )
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.clientId = :clientId
            """)
    List<CardResponseDto> findCardsWithBalanceByClientId(@Param("clientId") UUID clientId);

    @Query("""
                SELECT new com.pers.dto.response.CardResponseDto(
                    c.id,
                    c.clientId,
                    c.accountId,
                    a.balance,
                    c.createdDate,
                    c.expireDate,
                    c.name,
                    c.currency,
                    c.status,
                    c.cardNumber
                )
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.id = :id
            """)
    Optional<CardResponseDto> findCardWithBalanceById(@Param("id") UUID id);

    @Query("""
                SELECT new com.pers.dto.response.CardResponseDto(
                    c.id,
                    c.clientId,
                    c.accountId,
                    a.balance,
                    c.createdDate,
                    c.expireDate,
                    c.name,
                    c.currency,
                    c.status,
                    c.cardNumber
                )
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.cardNumber = :number
            """)
    Optional<CardResponseDto> findByNumber(@Param("number") String number);

    @Query("""
                SELECT new com.pers.dto.response.CardResponseDto(
                    c.id,
                    c.clientId,
                    c.accountId,
                    a.balance,
                    c.createdDate,
                    c.expireDate,
                    c.name,
                    c.currency,
                    c.status,
                    c.cardNumber
                )
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.accountId = :accountId
            """)
    List<CardResponseDto> findByAccountId(@Param("accountId") UUID accountId);

    boolean existsByCardNumber(String cardNumber);
}
