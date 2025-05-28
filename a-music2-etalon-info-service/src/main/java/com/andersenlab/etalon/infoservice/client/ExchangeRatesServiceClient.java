package com.andersenlab.etalon.infoservice.client;

import com.andersenlab.etalon.infoservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    url = "${feign.exchange-rates.url}",
    path = "/service/data/EXR",
    name = "exchange-rates-api",
    configuration = FeignConfig.class)
public interface ExchangeRatesServiceClient {
  @GetMapping(
      headers = "Accept: " + MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE,
      value = "/{currencies}")
  String getExchangeRates(
      @PathVariable String currencies, @RequestParam(name = "startPeriod") String param);
}
