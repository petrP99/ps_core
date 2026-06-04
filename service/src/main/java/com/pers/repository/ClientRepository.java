package com.pers.repository;

import com.pers.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.UUID;


public interface ClientRepository extends JpaRepository<Client, UUID>,
        FilterClientRepository,
        QuerydslPredicateExecutor<Client> {

    Optional<Client> findById(UUID id);

    Optional<Client> findByPhone(String phone);

    @Query("select concat(c.firstName, ' ', c.lastName) from Client c where c.id = :id")
    String findFirstAndLastNameByClientId(UUID id);
}