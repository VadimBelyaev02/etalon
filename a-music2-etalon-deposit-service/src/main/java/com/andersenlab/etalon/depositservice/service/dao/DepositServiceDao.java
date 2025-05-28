package com.andersenlab.etalon.depositservice.service.dao;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import com.andersenlab.etalon.depositservice.util.filter.DepositFilterRequest;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepositServiceDao {

  DepositEntity save(DepositEntity depositEntity);

  List<DepositEntity> saveAll(List<DepositEntity> deposits);

  List<DepositEntity> findAllByExample(Example<DepositEntity> depositExample);

  List<DepositEntity> findAllByStatusNot(DepositStatus status);

  Page<DepositEntity> findAllByUserId(Pageable pageable, String userId);

  DepositEntity findByIdAndUserId(Long id, String userId);

  Page<DepositEntity> findFilteredDeposits(
      String userId, CustomPageRequest pageRequest, DepositFilterRequest filter);
}
