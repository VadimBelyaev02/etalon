package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.TransferTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferTypeRepository extends JpaRepository<TransferTypeEntity, Long> {}
