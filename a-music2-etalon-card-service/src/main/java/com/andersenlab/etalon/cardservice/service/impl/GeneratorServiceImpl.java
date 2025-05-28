package com.andersenlab.etalon.cardservice.service.impl;

import com.andersenlab.etalon.cardservice.service.GeneratorService;
import com.andersenlab.etalon.cardservice.util.GeneratorUtils;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import org.springframework.stereotype.Service;

@Service
public class GeneratorServiceImpl implements GeneratorService {
  private static final String VISA = "4";
  private static final String MASTERCARD = "5";
  private static final int CVV_LENGTH = 3;

  @Override
  public String generateCardNumber(Issuer issuer, Long serialNumber) {
    if (issuer.equals(Issuer.VISA)) {
      return GeneratorUtils.generateCardNumber(VISA, serialNumber);
    }
    return GeneratorUtils.generateCardNumber(MASTERCARD, serialNumber);
  }

  @Override
  public Integer generateCVV() {
    return Integer.valueOf(GeneratorUtils.generateRandomNumber(CVV_LENGTH));
  }
}
