package com.pers.repository;

import com.pers.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface TransferRepository extends JpaRepository<Transfer, Long>,
        FilterTransferRepository,
        QuerydslPredicateExecutor<Transfer> {

}
