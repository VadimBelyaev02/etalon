package com.andersenlab.etalon.depositservice.repository;

import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositProductRepository extends JpaRepository<DepositProductEntity, Long> {

  List<DepositProductEntity> findAllByOrderByCreateAtAsc();
}
