package com.andersenlab.etalon.depositservice.service.dao;

import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import java.util.List;

public interface DepositProductServiceDao {

  DepositProductEntity findById(Long depositProductId);

  List<DepositProductEntity> findAllByOrderByCreateAtAsc();
}
