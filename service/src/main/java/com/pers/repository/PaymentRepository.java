package com.pers.repository;

import com.pers.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID>,
        FilterPaymentRepository,
        QuerydslPredicateExecutor<Payment> {

    Optional<Payment> findByIdAndClientId(UUID id, UUID clientId);

}
