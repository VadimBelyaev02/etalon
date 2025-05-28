package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import java.util.List;

public interface ExchangeRatesCalculationService {

  List<ExchangeRateResponseDto> getCurrencyRates(String currency);
}
