package com.andersenlab.etalon.loanservice.service.impl;

import com.andersenlab.etalon.loanservice.dto.loan.response.LoanProductResponseDto;
import com.andersenlab.etalon.loanservice.mapper.LoanProductMapper;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.LoanProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanProductServiceImpl implements LoanProductService {

  private final LoanProductRepository loanProductRepository;
  private final LoanProductMapper loanProductMapper;

  @Override
  public List<LoanProductResponseDto> getAllLoanProducts() {
    return loanProductMapper.listOfLoanTypesToListOfDtos(loanProductRepository.findAll());
  }
}
