package com.andersenlab.etalon.accountservice.service.impl;

import static com.andersenlab.etalon.accountservice.util.GeneratorUtils.generateAccountNumber;
import static com.andersenlab.etalon.accountservice.util.GeneratorUtils.getChecksum;

import com.andersenlab.etalon.accountservice.service.GeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl implements GeneratorService {
  private static final String COUNTRY_PREFIX = "PL";
  private static final String BANK_CODE = "234";
  private static final String BRANCH_CODE = "5678";
  private static final String COUNTRY_CODE = "4";

  @Override
  public String generateIban(Long serialNumber) {

    StringBuilder result = new StringBuilder(COUNTRY_PREFIX);

    String defaultPartOfAccountNumber = BANK_CODE + BRANCH_CODE + COUNTRY_CODE;
    String accountNumber = generateAccountNumber(serialNumber);
    String checkSum = getChecksum(defaultPartOfAccountNumber.concat(accountNumber));
    result.append(checkSum).append(defaultPartOfAccountNumber).append(accountNumber);

    return result.toString();
  }
}
