package com.andersenlab.etalon.infoservice.controller.impl;

import com.andersenlab.etalon.infoservice.controller.ExchangeRatesController;
import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import com.andersenlab.etalon.infoservice.service.ExchangeRatesCalculationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ExchangeRatesController.BANK_INFO_EXCHANGE_RATES_URL)
@RequiredArgsConstructor
@Tag(name = "Exchange Rates")
public class ExchangeRatesControllerImpl implements ExchangeRatesController {

  private final ExchangeRatesCalculationService exchangeCalculationService;

  @GetMapping(EXCHANGE_RATES_URI)
  public List<ExchangeRateResponseDto> getExchangeRates(
      @RequestParam(required = false, defaultValue = DEFAULT_CURRENCY) String currency) {
    return exchangeCalculationService.getCurrencyRates(currency);
  }
}
