package com.pers.repository;

import com.pers.dto.filter.PaymentFilterDto;
import com.pers.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FilterPaymentRepository {

    Page<Payment> findAllByFilter(PaymentFilterDto filterDto, Pageable pageable);

    Page<Payment> findAllByClientByFilter(PaymentFilterDto filterDto, Pageable pageable, UUID clientId);

}