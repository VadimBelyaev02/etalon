package com.andersenlab.etalon.depositservice.service.dao;

import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;

public interface DepositOrderServiceDao {
  DepositOrderEntity save(DepositOrderEntity depositOrder);

  DepositOrderEntity findById(Long id);

  DepositOrderEntity findDepositOrderEntityByTransactionId(Long transactionId);
}
