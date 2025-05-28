package com.andersenlab.etalon.transactionservice.util.exchange_rate;

import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;

@UtilityClass
public class ExchangeRateUtil {

  public static BigDecimal findRate(
      CurrencyName currency,
      List<ExchangeRateResponseDto> exchangeRates,
      Function<ExchangeRateResponseDto, BigDecimal> rateGetter) {

    return exchangeRates.stream()
        .filter(rate -> rate.currencyName().equals(currency))
        .map(rateGetter)
        .findFirst()
        .orElseThrow(() -> createExchangeRateNotFoundException(currency));
  }

  private static BusinessException createExchangeRateNotFoundException(CurrencyName currency) {
    return new BusinessException(
        HttpStatus.NOT_FOUND, BusinessException.NOT_FOUND_EXCHANGE_RATES.formatted(currency));
  }
}
