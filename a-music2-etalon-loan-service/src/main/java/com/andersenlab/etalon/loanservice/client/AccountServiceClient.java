package com.andersenlab.etalon.loanservice.client;

import com.andersenlab.etalon.loanservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.loanservice.dto.account.request.AccountBalanceRequestDto;
import com.andersenlab.etalon.loanservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

  String ACCOUNT_URL = API_V1_URI + ACCOUNTS_URI;
  String ACCOUNT_ID_PATH_URL = API_V1_URI + ACCOUNTS_URI + ACCOUNT_NUMBER_PATH;

  String ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL =
      API_V1_URI + ACCOUNT_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH;
  String ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL =
      API_V1_URI + ACCOUNT_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH + BALANCE_URI;

  @PostMapping(ACCOUNT_URL)
  AccountResponseDto createAccount(@RequestBody AccountCreationRequestDto accountCreation);

  @PatchMapping(ACCOUNT_ID_PATH_URL)
  MessageResponseDto updateAccount(
      @PathVariable Long accountNumber,
      @RequestBody final AccountBalanceRequestDto accountBalanceRequestDto);

  @GetMapping(ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(@PathVariable String accountNumber);

  @GetMapping(ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountDetailedResponseDto getDetailedAccountInfo(@PathVariable String accountNumber);
}
