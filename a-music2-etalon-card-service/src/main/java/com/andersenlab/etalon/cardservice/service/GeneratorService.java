package com.andersenlab.etalon.cardservice.service;

import com.andersenlab.etalon.cardservice.util.enums.Issuer;

public interface GeneratorService {

  String generateCardNumber(Issuer issuer, Long serialNumber);

  Integer generateCVV();
}
