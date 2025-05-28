package com.andersenlab.etalon.depositservice.service.dao;

import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import java.time.ZonedDateTime;
import java.util.List;

public interface DepositInterestServiceDao {

  List<DepositInterestEntity> saveAllInterests(List<DepositInterestEntity> depositInterestEntities);

  List<DepositInterestEntity> findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
      List<Long> listId, ZonedDateTime createAt);

  List<DepositInterestEntity> findAllByDepositId(Long depositId);
}
