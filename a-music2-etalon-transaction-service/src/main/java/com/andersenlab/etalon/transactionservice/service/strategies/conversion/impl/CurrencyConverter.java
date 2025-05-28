package com.andersenlab.etalon.transactionservice.service.strategies.conversion.impl;

import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyConverter {

  private final DirectConversionStrategy directConversionStrategy;
  private final DoubleConversionStrategy doubleConversionStrategy;

  public BigDecimal calculateStandardRate(
      CurrencyName senderCurrency,
      CurrencyName beneficiaryCurrency,
      List<ExchangeRateResponseDto> exchangeRates) {

    if (senderCurrency.equals(beneficiaryCurrency)) {
      return null;
    }

    if (senderCurrency.equals(CurrencyName.PLN) || beneficiaryCurrency.equals(CurrencyName.PLN)) {
      return directConversionStrategy.calculateRate(
          senderCurrency, beneficiaryCurrency, exchangeRates);
    } else {
      return doubleConversionStrategy.calculateRate(
          senderCurrency, beneficiaryCurrency, exchangeRates);
    }
  }
}
