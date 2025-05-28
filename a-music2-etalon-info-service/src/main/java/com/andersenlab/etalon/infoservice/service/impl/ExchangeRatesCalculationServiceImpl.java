package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.config.ExchangeCoefficientsProperties;
import com.andersenlab.etalon.infoservice.config.TimeProvider;
import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.service.ExchangeRatesCalculationService;
import com.andersenlab.etalon.infoservice.service.ExchangeRatesRequestService;
import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRatesCalculationServiceImpl implements ExchangeRatesCalculationService {
  private final ExchangeCoefficientsProperties coefficientsProperties;
  private final TimeProvider timeProvider;
  private final ExchangeRatesRequestService exchangeRatesRequestService;
  private Map<String, Double> exchangeRates;

  public List<ExchangeRateResponseDto> getCurrencyRates(String baseCurrency) {

    try {
      CurrencyName.valueOf(baseCurrency);
    } catch (IllegalArgumentException e) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.CURRENCY_NOT_FOUND.formatted(baseCurrency));
    }

    exchangeRates = exchangeRatesRequestService.getExchangeRates();

    List<ExchangeRateResponseDto> rates = new ArrayList<>();

    for (CurrencyName quoteCurrency : CurrencyName.values()) {
      if (!quoteCurrency.name().equals(baseCurrency)) {
        final double rate = getRate(baseCurrency, quoteCurrency.name());
        final BigDecimal sellingRate =
            coefficientsProperties
                .getBuy()
                .multiply(BigDecimal.valueOf(rate))
                .setScale(4, RoundingMode.CEILING);
        final BigDecimal buyingRate =
            coefficientsProperties
                .getSell()
                .multiply(BigDecimal.valueOf(rate))
                .setScale(4, RoundingMode.CEILING);
        final ZonedDateTime updateAt = timeProvider.getCurrentZonedDateTime();
        rates.add(
            new ExchangeRateResponseDto(
                quoteCurrency, quoteCurrency.getId(), sellingRate, buyingRate, updateAt));
      }
    }
    return rates;
  }

  private double getRate(String baseCurrency, String quoteCurrency) {
    if (baseCurrency.equals(CurrencyName.EUR.name())) {
      return exchangeRates.get(quoteCurrency);
    }
    if (quoteCurrency.equals(CurrencyName.EUR.name())) {
      return 1 / exchangeRates.get(baseCurrency);
    } else {
      return exchangeRates.get(quoteCurrency) / exchangeRates.get(baseCurrency);
    }
  }
}
