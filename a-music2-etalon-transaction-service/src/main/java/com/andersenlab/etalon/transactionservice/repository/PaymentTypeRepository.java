package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.PaymentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentTypeEntity, Long> {}
