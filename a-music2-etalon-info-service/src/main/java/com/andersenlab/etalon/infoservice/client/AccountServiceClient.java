package com.andersenlab.etalon.infoservice.client;

import com.andersenlab.etalon.infoservice.config.FeignConfig;
import com.andersenlab.etalon.infoservice.dto.response.AccountDetailedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "account-service",
    url = "${feign.account-service.url}",
    configuration = FeignConfig.class,
    path = "/account")
public interface AccountServiceClient {
  String API_V1_URI = "/api/v1";
  String ACCOUNT_BY_NUMBER_URI = "/accounts-by-number";
  String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  String ACCOUNT_NUMBER_URL = API_V1_URI + ACCOUNT_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH;

  @GetMapping(ACCOUNT_NUMBER_URL)
  AccountDetailedResponseDto getDetailedAccountInfo(@PathVariable String accountNumber);
}
