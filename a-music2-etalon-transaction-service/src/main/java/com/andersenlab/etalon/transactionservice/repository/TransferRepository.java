package com.andersenlab.etalon.transactionservice.repository;

import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
  Optional<TransferEntity> findByTransactionId(Long transactionId);
}
