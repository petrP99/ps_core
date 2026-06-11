package com.pers.repository;

import com.pers.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

//    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM Account a WHERE a.clientId = :clientId")
//    BigDecimal getTotalBalanceByClientId(@Param("clientId") UUID clientId);

    List<Account> findAllByClientId(UUID clientId);

    Optional<Account> findByIdAndClientId(UUID id, UUID clientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") UUID id);
}
