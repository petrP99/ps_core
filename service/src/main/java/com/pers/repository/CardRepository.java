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
        FilterCardRepository,
        QuerydslPredicateExecutor<Card> {

    List<Card> findByClientId(UUID clientId);

    @Query("""
                SELECT 
                    c.id as id, 
                    c.clientId as clientId, 
                    c.accountId as accountId, 
                    c.createdDate as createdDate, 
                    c.expireDate as expireDate, 
                    c.name as name, 
                    c.currency as currency, 
                    c.status as status, 
                    a.balance as balance,
                    c.cardNumber as cardNumber 
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.clientId = :clientId
            """)
    List<CardResponseDto> findCardsWithBalanceByClientId(@Param("clientId") UUID clientId);

    @Query("""
                SELECT 
                    c.id as id, 
                    c.clientId as clientId, 
                    c.accountId as accountId, 
                    c.createdDate as createdDate, 
                    c.expireDate as expireDate, 
                    c.name as name, 
                    c.currency as currency, 
                    c.status as status, 
                    a.balance as balance,
                    c.cardNumber as cardNumber 
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.id = :id
            """)
    Optional<CardResponseDto> findCardWithBalanceById(@Param("id") UUID id);

    @Query("""
                SELECT 
                    c.id as id, 
                    c.clientId as clientId, 
                    c.accountId as accountId, 
                    c.createdDate as createdDate, 
                    c.expireDate as expireDate, 
                    c.name as name, 
                    c.currency as currency, 
                    c.status as status, 
                    a.balance as balance,
                    c.cardNumber as cardNumber 
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.cardNumber = :id
            """)
    Optional<CardResponseDto> findByNumber(@Param("id")String id);

//    List<Card> findByClientPhone(String phone); // todo

    @Query("""
                SELECT 
                    c.id as id, 
                    c.cardNumber as cardNumber, 
                    c.clientId as clientId, 
                    c.accountId as accountId, 
                    c.createdDate as createdDate, 
                    c.expireDate as expireDate, 
                    c.name as name, 
                    c.currency as currency, 
                    c.status as status, 
                    a.balance as balance
                FROM Card c
                JOIN Account a ON c.accountId = a.id
                WHERE c.accountId = :accountId
            """)
    List<CardResponseDto> findByAccountId(@Param("accountId")UUID accountId);

    boolean existsByCardNumber(String cardNumber);
}