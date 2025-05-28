package com.andersenlab.etalon.transactionservice.service.strategies.conversion.impl;

import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.service.strategies.conversion.ConversionStrategy;
import com.andersenlab.etalon.transactionservice.util.DecimalUtils;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.exchange_rate.ExchangeRateUtil;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DirectConversionStrategy implements ConversionStrategy {

  @Override
  public BigDecimal calculateRate(
      CurrencyName senderCurrency,
      CurrencyName beneficiaryCurrency,
      List<ExchangeRateResponseDto> exchangeRates) {

    if (senderCurrency.equals(CurrencyName.PLN)) {
      return ExchangeRateUtil.findRate(
          beneficiaryCurrency, exchangeRates, ExchangeRateResponseDto::sellingRate);
    }

    BigDecimal buyingRate =
        ExchangeRateUtil.findRate(
            senderCurrency, exchangeRates, ExchangeRateResponseDto::buyingRate);

    return DecimalUtils.divide(BigDecimal.ONE, buyingRate);
  }
}
