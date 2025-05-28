package com.andersenlab.etalon.infoservice.controller;

import com.andersenlab.etalon.infoservice.dto.response.ExchangeRateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;

public interface ExchangeRatesController {
  String DEFAULT_CURRENCY = "PLN";
  String EXCHANGE_RATES_URI = "/exchange-rates";
  String API_V1_URI = "/api/v1";
  String INFO_URI = "/info";
  String BANK_INFO_EXCHANGE_RATES_URL = API_V1_URI + INFO_URI;
  String EXCHANGE_RATES_URL = BANK_INFO_EXCHANGE_RATES_URL + EXCHANGE_RATES_URI;

  @Operation(summary = "Get exchange rates — US 1.11")
  @ApiResponse(
      responseCode = "200",
      description = "Information provided",
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ExchangeRateResponseDto.class)))
  List<ExchangeRateResponseDto> getExchangeRates(
      @RequestParam(required = false, defaultValue = DEFAULT_CURRENCY) String currency);
}
