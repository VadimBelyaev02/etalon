package com.andersenlab.etalon.infoservice.service;

import java.util.Map;

public interface ExchangeRatesRequestService {

  Map<String, Double> getExchangeRates();
}
