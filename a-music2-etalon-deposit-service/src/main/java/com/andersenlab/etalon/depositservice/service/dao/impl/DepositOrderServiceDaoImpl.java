package com.andersenlab.etalon.depositservice.service.dao.impl;

import static com.andersenlab.etalon.depositservice.exception.BusinessException.DEPOSIT_ORDER_NOT_FOUND_BY_TRANSACTION_ID;

import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.repository.DepositOrderRepository;
import com.andersenlab.etalon.depositservice.service.dao.DepositOrderServiceDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositOrderServiceDaoImpl implements DepositOrderServiceDao {
  private final DepositOrderRepository depositOrderRepository;

  @Override
  public DepositOrderEntity save(DepositOrderEntity depositOrderEntity) {
    return depositOrderRepository.save(depositOrderEntity);
  }

  @Override
  public DepositOrderEntity findById(Long id) {
    return depositOrderRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(BusinessException.DEPOSIT_ORDER_NOT_FOUND_BY_ID, id)));
  }

  @Override
  public DepositOrderEntity findDepositOrderEntityByTransactionId(Long transactionId) {
    return depositOrderRepository
        .findDepositOrderEntityByTransactionId(transactionId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    DEPOSIT_ORDER_NOT_FOUND_BY_TRANSACTION_ID.formatted(transactionId)));
  }
}
