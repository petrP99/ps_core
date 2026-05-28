package com.pers.repository;

import com.pers.entity.Transfer;
import com.pers.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;


public interface TransferRepository extends JpaRepository<Transfer, Long>,
        FilterTransferRepository,
        QuerydslPredicateExecutor<Transfer> {

}
