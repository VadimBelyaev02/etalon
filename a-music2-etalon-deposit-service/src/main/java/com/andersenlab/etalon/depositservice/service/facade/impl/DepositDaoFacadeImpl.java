package com.andersenlab.etalon.depositservice.service.facade.impl;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import com.andersenlab.etalon.depositservice.service.dao.DepositInterestServiceDao;
import com.andersenlab.etalon.depositservice.service.dao.DepositOrderServiceDao;
import com.andersenlab.etalon.depositservice.service.dao.DepositProductServiceDao;
import com.andersenlab.etalon.depositservice.service.dao.DepositServiceDao;
import com.andersenlab.etalon.depositservice.service.facade.DepositDaoFacade;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositDaoFacadeImpl implements DepositDaoFacade {
  private final DepositServiceDao depositServiceDao;
  private final DepositInterestServiceDao depositInterestServiceDao;
  private final DepositProductServiceDao depositProductServiceDao;
  private final DepositOrderServiceDao depositOrderServiceDao;

  @Override
  public List<DepositInterestEntity> findAllInterestsByDepositId(Long depositId) {
    return depositInterestServiceDao.findAllByDepositId(depositId);
  }

  @Override
  public List<DepositInterestEntity> findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
      List<Long> depositIds, ZonedDateTime createAt) {
    return depositInterestServiceDao.findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
        depositIds, createAt);
  }

  @Override
  public Page<DepositEntity> findAllDepositsByUserId(Pageable pageable, String userId) {
    return depositServiceDao.findAllByUserId(pageable, userId);
  }

  @Override
  public DepositEntity findDepositByIdAndUserId(Long depositId, String userId) {
    return depositServiceDao.findByIdAndUserId(depositId, userId);
  }

  @Override
  public DepositEntity saveDeposit(DepositEntity depositEntity) {
    return depositServiceDao.save(depositEntity);
  }

  @Override
  public DepositOrderEntity saveDepositOrder(DepositOrderEntity depositOrderEntity) {
    return depositOrderServiceDao.save(depositOrderEntity);
  }

  @Override
  public DepositProductEntity findDepositProductById(Long depositProductId) {
    return depositProductServiceDao.findById(depositProductId);
  }

  @Override
  public Page<DepositEntity> findFilteredDepositsByUserId(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter) {
    return depositServiceDao.findFilteredDeposits(userId, pageRequest, filter);
  }
}
