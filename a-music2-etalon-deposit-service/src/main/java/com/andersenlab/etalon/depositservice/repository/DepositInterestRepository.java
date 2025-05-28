package com.andersenlab.etalon.depositservice.repository;

import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositInterestRepository extends JpaRepository<DepositInterestEntity, Long> {

  List<DepositInterestEntity> findAllByDepositId(Long depositId);

  List<DepositInterestEntity> findAllByDepositIdInAndCreateAtGreaterThanEqual(
      List<Long> depositIds, ZonedDateTime createAt);
}
