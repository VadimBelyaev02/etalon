package com.andersenlab.etalon.transactionservice.client;

import com.andersenlab.etalon.transactionservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "account-service",
    url = "${feign.account-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/account")
public interface AccountServiceClient {
  String API_V1_URI = "/api/v1";
  String API_INT_V1_URI = "/internal/api/v1";
  String ACCOUNTS_URI = "/accounts";
  String ACCOUNTS_BY_NUMBER_URI = "/accounts-by-number";
  String BALANCE_URI = "/balance";
  String REPLENISHED_URI = "/replenished";
  String WITHDRAWN_URI = "/withdrawn";
  String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  String ACCOUNTS_URL = API_V1_URI + ACCOUNTS_URI;
  String ACCOUNT_NUMBER_WITHDRAWN_PATH_URL_INTERNAL_CALL =
      API_INT_V1_URI + ACCOUNTS_URI + ACCOUNT_NUMBER_PATH + WITHDRAWN_URI;
  String ACCOUNT_NUMBER_REPLENISHED_PATH_URL_INTERNAL_CALL =
      API_INT_V1_URI + ACCOUNTS_URI + ACCOUNT_NUMBER_PATH + REPLENISHED_URI;
  String ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL =
      API_V1_URI + ACCOUNTS_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH;
  String ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL =
      API_V1_URI + ACCOUNTS_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH + BALANCE_URI;
  String ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL_INTERNAL_CALL =
      API_INT_V1_URI + ACCOUNTS_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH;

  @GetMapping(ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(@PathVariable String accountNumber);

  @GetMapping(ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountDetailedResponseDto getDetailedAccountInfo(@PathVariable String accountNumber);

  @PostMapping(ACCOUNT_NUMBER_REPLENISHED_PATH_URL_INTERNAL_CALL)
  MessageResponseDto replenishAccountBalance(
      @PathVariable String accountNumber,
      @RequestBody
          final AccountReplenishByAccountNumberRequestDto
              accountReplenishByAccountNumberRequestDto);

  @PostMapping(ACCOUNT_NUMBER_WITHDRAWN_PATH_URL_INTERNAL_CALL)
  MessageResponseDto withdrawAccountBalance(
      @PathVariable String accountNumber,
      @RequestBody
          final AccountWithdrawByAccountNumberRequestDto accountWithdrawByAccountNumberRequestDto);

  @GetMapping(ACCOUNTS_URL)
  List<AccountNumberResponseDto> getAllAccountNumbers(@RequestParam String userId);

  @GetMapping(ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL_INTERNAL_CALL)
  AccountDetailedResponseDto getDetailedAccountInfoInternalCall(@PathVariable String accountNumber);
}
