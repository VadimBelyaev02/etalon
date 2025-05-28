package com.andersenlab.etalon.depositservice.repository;

import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositOrderRepository extends JpaRepository<DepositOrderEntity, Long> {
  Optional<DepositOrderEntity> findDepositOrderEntityByTransactionId(Long transactionId);
}
