package com.andersenlab.etalon.depositservice.service.dao.impl;

import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.repository.DepositInterestRepository;
import com.andersenlab.etalon.depositservice.service.dao.DepositInterestServiceDao;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositInterestServiceDaoImpl implements DepositInterestServiceDao {
  private final DepositInterestRepository depositInterestRepository;

  @Override
  public List<DepositInterestEntity> saveAllInterests(
      List<DepositInterestEntity> depositInterestEntities) {
    return depositInterestRepository.saveAll(depositInterestEntities);
  }

  @Override
  public List<DepositInterestEntity> findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
      List<Long> listId, ZonedDateTime createAt) {
    return depositInterestRepository.findAllByDepositIdInAndCreateAtGreaterThanEqual(
        listId, createAt);
  }

  @Override
  public List<DepositInterestEntity> findAllByDepositId(Long depositId) {
    return depositInterestRepository.findAllByDepositId(depositId);
  }
}
