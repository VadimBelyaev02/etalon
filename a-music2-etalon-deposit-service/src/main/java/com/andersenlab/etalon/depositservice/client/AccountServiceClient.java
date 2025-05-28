package com.andersenlab.etalon.depositservice.client;

import com.andersenlab.etalon.depositservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.depositservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  String ACCOUNTS_URI = "/accounts";
  String ACCOUNT_BY_NUMBER_URI = "/accounts-by-number";
  String BALANCE_URI = "/balance";
  String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  String ACCOUNT_URL = API_V1_URI + ACCOUNTS_URI;
  String ACCOUNT_BY_NUMBER_URL = API_V1_URI + ACCOUNT_BY_NUMBER_URI;
  String ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL = ACCOUNT_BY_NUMBER_URL + ACCOUNT_NUMBER_PATH;
  String ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL =
      ACCOUNT_BY_NUMBER_URL + ACCOUNT_NUMBER_PATH + BALANCE_URI;

  @PostMapping(ACCOUNT_URL)
  AccountResponseDto createAccount(@RequestBody AccountCreationRequestDto accountCreation);

  @GetMapping(ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(@PathVariable String accountNumber);

  @GetMapping(ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL)
  AccountDetailedResponseDto getDetailedAccountInfo(@PathVariable String accountNumber);

  @GetMapping(ACCOUNT_BY_NUMBER_URL)
  List<AccountInterestResponseDto> getAccountsBalances(@RequestParam List<String> accountsNumbers);

  @DeleteMapping(ACCOUNT_INFO_BY_ACCOUNT_NUMBER_PATH_URL)
  void deleteAccount(@PathVariable String accountNumber);
}
