package com.pers.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processed_balance_operation")
public class ProcessedBalanceOperation {

    @Id
    @Column(name = "operation_id")
    private UUID operationId;

    @Column(nullable = false)
    private boolean successful;

    @Column(name = "failure_code", length = 80)
    private String failureCode;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
