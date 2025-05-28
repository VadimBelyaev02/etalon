package com.andersenlab.etalon.transactionservice.service.strategies.conversion;

import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.util.List;

public interface ConversionStrategy {
  BigDecimal calculateRate(
      CurrencyName senderCurrency,
      CurrencyName beneficiaryCurrency,
      List<ExchangeRateResponseDto> exchangeRates);
}
