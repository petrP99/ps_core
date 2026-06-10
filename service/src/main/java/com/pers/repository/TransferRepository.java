package com.pers.repository;

import com.pers.entity.Transfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.util.UUID;


public interface TransferRepository extends JpaRepository<Transfer, UUID>,
        FilterTransferRepository,
        QuerydslPredicateExecutor<Transfer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transfer t WHERE t.id = :id")
    Optional<Transfer> findByIdForUpdate(@Param("id") UUID id);

    Optional<Transfer> findByIdAndFromClientId(UUID id, UUID fromClientId);

    @Query("""
            SELECT t
            FROM Transfer t
            WHERE t.fromClientId = :clientId OR t.toClientId = :clientId
            ORDER BY t.timeOfTransfer DESC
            """)
    List<Transfer> findAllByParticipantOrderByTimeOfTransferDesc(
            @Param("clientId") UUID clientId
    );

    @Query("""
            SELECT t
            FROM Transfer t
            WHERE t.id = :id
              AND (t.fromClientId = :clientId OR t.toClientId = :clientId)
            """)
    Optional<Transfer> findByIdAndParticipant(
            @Param("id") UUID id,
            @Param("clientId") UUID clientId
    );
}
