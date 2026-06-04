package com.pers.repository;

import com.pers.entity.Replenishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;


public interface ReplenishmentRepository extends JpaRepository<Replenishment, Long>,
        FilterReplenishmentRepository,
        QuerydslPredicateExecutor<Replenishment> {

    List<Replenishment> findAllByClientId(UUID id);

}