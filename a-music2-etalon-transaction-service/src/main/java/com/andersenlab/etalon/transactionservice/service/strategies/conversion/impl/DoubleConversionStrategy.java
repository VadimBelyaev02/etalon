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
public class DoubleConversionStrategy implements ConversionStrategy {

  @Override
  public BigDecimal calculateRate(
      CurrencyName senderCurrency,
      CurrencyName beneficiaryCurrency,
      List<ExchangeRateResponseDto> exchangeRates) {

    BigDecimal rateToPLN =
        ExchangeRateUtil.findRate(
            senderCurrency, exchangeRates, ExchangeRateResponseDto::buyingRate);

    BigDecimal rateFromPLN =
        ExchangeRateUtil.findRate(
            beneficiaryCurrency, exchangeRates, ExchangeRateResponseDto::sellingRate);

    return DecimalUtils.multiply(DecimalUtils.divide(BigDecimal.ONE, rateToPLN), rateFromPLN);
  }
}
