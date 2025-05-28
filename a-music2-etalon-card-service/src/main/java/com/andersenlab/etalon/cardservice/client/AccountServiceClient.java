package com.andersenlab.etalon.cardservice.client;

import com.andersenlab.etalon.cardservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.cardservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "account-service",
    url = "${feign.account-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/account")
public interface AccountServiceClient {
  String API_V1_URI = "/api/v1";
  String ACCOUNTS_URI = "/accounts";
  String ACCOUNT_BY_NUMBER_URI = "/accounts-by-number";
  String BALANCE_URI = "/balance";
  String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  String BLOCKING_URI = "/blocking";
  String ACCOUNTS_URL = API_V1_URI + ACCOUNTS_URI;
  String ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL =
      API_V1_URI + ACCOUNT_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH + BALANCE_URI;
  String ACCOUNTS_BLOCKING = API_V1_URI + ACCOUNT_BY_NUMBER_URI + BLOCKING_URI;
  String API_INT_V1_URI = "/internal/api/v1";
  String ACCOUNTS_BY_NUMBER_URI = "/accounts-by-number";
  String ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL_INTERNAL_CALL =
      API_INT_V1_URI + ACCOUNTS_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH;

  @PostMapping(ACCOUNTS_URL)
  AccountResponseDto createAccount(@RequestBody AccountCreationRequestDto accountCreation);

  @GetMapping(ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(@PathVariable String accountNumber);

  @PutMapping(ACCOUNTS_BLOCKING)
  Boolean changeIsBlocked(@RequestParam String accountNumber);

  @GetMapping(ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL_INTERNAL_CALL)
  AccountDetailedResponseDto getDetailedAccountInfoInternalCall(@PathVariable String accountNumber);
}
