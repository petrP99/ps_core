package com.pers.repository;

import com.pers.entity.ProcessedBalanceOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedBalanceOperationRepository extends JpaRepository<ProcessedBalanceOperation, UUID> {
}
