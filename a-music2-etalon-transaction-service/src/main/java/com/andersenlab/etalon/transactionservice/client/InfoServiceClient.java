package com.andersenlab.etalon.transactionservice.client;

import com.andersenlab.etalon.transactionservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.transactionservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.transactionservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.BankInfoResponseDto;
import com.andersenlab.etalon.transactionservice.dto.info.response.ExchangeRateResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "info-service",
    url = "${feign.info-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/info")
public interface InfoServiceClient {

  String EXCHANGE_RATES_URI = "/exchange-rates";
  String BANKS_URI = "/banks";
  String SEARCH_URI = "/search";
  String API_V1_URI = "/api/v1";
  String INFO_URI = "/info";
  String BANK_INFO_EXCHANGE_RATES_URL = API_V1_URI + INFO_URI + EXCHANGE_RATES_URI;
  String BANKS_INFO_SEARCH_URL = API_V1_URI + INFO_URI + BANKS_URI + SEARCH_URI;
  String CONFIRMATIONS_URI = "/confirmations";
  String CONFIRMATIONS_URL = API_V1_URI + CONFIRMATIONS_URI;

  @GetMapping(BANK_INFO_EXCHANGE_RATES_URL)
  List<ExchangeRateResponseDto> getExchangeRates(
      @RequestParam LinkedMultiValueMap<String, String> params);

  @GetMapping(BANKS_INFO_SEARCH_URL)
  BankInfoResponseDto getBankInfo(@RequestParam String iban);

  @PostMapping(CONFIRMATIONS_URL)
  CreateConfirmationResponseDto createConfirmation(
      @RequestBody CreateConfirmationRequestDto authConfirmation);
}
