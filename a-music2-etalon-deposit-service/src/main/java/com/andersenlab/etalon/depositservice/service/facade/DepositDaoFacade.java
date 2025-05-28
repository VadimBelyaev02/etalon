package com.andersenlab.etalon.depositservice.service.facade;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepositDaoFacade {
  List<DepositInterestEntity> findAllInterestsByDepositId(Long depositId);

  List<DepositInterestEntity> findAllInterestsByDepositIdInAndCreateAtGreaterThanEqual(
      List<Long> depositIds, ZonedDateTime createAt);

  Page<DepositEntity> findAllDepositsByUserId(Pageable pageable, String userId);

  DepositEntity findDepositByIdAndUserId(Long depositId, String userId);

  DepositEntity saveDeposit(DepositEntity depositEntity);

  DepositOrderEntity saveDepositOrder(DepositOrderEntity depositOrderEntity);

  DepositProductEntity findDepositProductById(Long depositProductId);

  Page<DepositEntity> findFilteredDepositsByUserId(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter);
}
