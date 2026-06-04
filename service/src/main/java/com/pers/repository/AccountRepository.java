package com.pers.repository;

import com.pers.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.clientId = :clientId")
    BigDecimal getTotalBalanceByClientId(@Param("clientId") UUID clientId);
}
