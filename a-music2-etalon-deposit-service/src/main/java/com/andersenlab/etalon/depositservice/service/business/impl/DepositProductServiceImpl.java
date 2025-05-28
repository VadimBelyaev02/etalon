package com.andersenlab.etalon.depositservice.service.business.impl;

import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositProductResponseDto;
import com.andersenlab.etalon.depositservice.mapper.DepositProductMapper;
import com.andersenlab.etalon.depositservice.service.business.DepositProductService;
import com.andersenlab.etalon.depositservice.service.dao.DepositProductServiceDao;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositProductServiceImpl implements DepositProductService {

  private final DepositProductServiceDao depositProductServiceDao;
  private final DepositProductMapper depositProductMapper;

  @Override
  public List<DepositProductResponseDto> getAllDepositProducts() {
    return depositProductMapper.toListOfDto(depositProductServiceDao.findAllByOrderByCreateAtAsc());
  }
}
