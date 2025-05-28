package com.andersenlab.etalon.depositservice.service.dao.impl;

import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.repository.DepositProductRepository;
import com.andersenlab.etalon.depositservice.service.dao.DepositProductServiceDao;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositProductServiceDaoImpl implements DepositProductServiceDao {
  private final DepositProductRepository depositProductRepository;

  @Override
  public DepositProductEntity findById(Long id) {
    return depositProductRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    String.format(BusinessException.DEPOSIT_PRODUCT_NOT_FOUND, id)));
  }

  @Override
  public List<DepositProductEntity> findAllByOrderByCreateAtAsc() {
    return depositProductRepository.findAllByOrderByCreateAtAsc();
  }
}
