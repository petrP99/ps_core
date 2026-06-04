package com.pers.repository;

import com.pers.entity.ExchangeRate;
import com.pers.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Currency> {

}

