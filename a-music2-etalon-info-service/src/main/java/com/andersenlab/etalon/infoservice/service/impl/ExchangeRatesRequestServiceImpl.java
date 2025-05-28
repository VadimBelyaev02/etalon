package com.andersenlab.etalon.infoservice.service.impl;

import com.andersenlab.etalon.infoservice.client.ExchangeRatesServiceClient;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.parser.Parser;
import com.andersenlab.etalon.infoservice.service.ExchangeRatesRequestService;
import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRatesRequestServiceImpl implements ExchangeRatesRequestService {
  private static final String FORMATTED_DATE =
      ZonedDateTime.now().minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  private final Parser parser;
  private final ExchangeRatesServiceClient exchangeRatesServiceClient;

  @Override
  @Cacheable(value = "rates_response")
  public Map<String, Double> getExchangeRates() {
    String currencies =
        Arrays.stream(CurrencyName.values())
            .map(Enum::name)
            .filter(name -> !CurrencyName.EUR.name().equals(name))
            .collect(Collectors.joining("+"));
    try {
      return parser.parse(
          exchangeRatesServiceClient.getExchangeRates(
              "D.".concat(currencies).concat(".EUR.SP00.A"), FORMATTED_DATE));
    } catch (JsonProcessingException e) {
      throw new BusinessException(
          HttpStatus.INTERNAL_SERVER_ERROR, BusinessException.PARSING_FAILURE_OF_RECEIVED_RATES);
    }
  }
}
