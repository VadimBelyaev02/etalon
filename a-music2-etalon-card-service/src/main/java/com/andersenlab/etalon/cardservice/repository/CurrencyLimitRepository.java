package com.andersenlab.etalon.cardservice.repository;

import com.andersenlab.etalon.cardservice.entity.CurrencyLimitEntity;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyLimitRepository extends JpaRepository<CurrencyLimitEntity, Currency> {}
