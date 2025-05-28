package com.andersenlab.etalon.loanservice.service.impl;

import com.andersenlab.etalon.loanservice.service.GeneratorService;
import com.andersenlab.etalon.loanservice.util.GeneratorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {

  private static final String CONTRACT_PREFIX = "CN";

  @Override
  public String generateContractNumber(Long orderId) {
    StringBuilder result = new StringBuilder(CONTRACT_PREFIX);

    String contractNumber = GeneratorUtils.generateContractNumber(orderId);
    result.append(contractNumber);

    return result.toString();
  }
}
