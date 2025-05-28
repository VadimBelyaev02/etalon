package com.andersenlab.etalon.accountservice.controller.impl;

import com.andersenlab.etalon.accountservice.controller.AccountByNumberController;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.accountservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.accountservice.service.AccountService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AccountByNumberController.ACCOUNT_BY_NUMBER_URL)
@RequiredArgsConstructor
public class AccountByNumberControllerImpl implements AccountByNumberController {

  private final AccountService accountService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL)
  public AccountBalanceResponseDto getAccountBalanceByAccountNumber(
      @PathVariable String accountNumber) {
    return accountService.getAccountBalance(accountNumber, authenticationHolder.getUserId());
  }

  @GetMapping(ACCOUNT_NUMBER_PATH)
  public AccountDetailedResponseDto getDetailedAccountInfo(@PathVariable String accountNumber) {
    return accountService.getDetailedAccountInfo(accountNumber, authenticationHolder.getUserId());
  }

  @GetMapping
  public List<AccountInterestResponseDto> getAccountsBalances(
      @RequestParam List<String> accountsNumbers) {
    return accountService.getAccountsBalances(accountsNumbers, authenticationHolder.getUserId());
  }

  @PutMapping(BLOCKING_URI)
  public Boolean changeIsBlocked(@RequestParam String accountNumber) {
    return accountService.changeIsBlocked(accountNumber, authenticationHolder.getUserId());
  }

  @DeleteMapping(ACCOUNT_NUMBER_PATH)
  public void deleteAccount(@PathVariable String accountNumber) {
    accountService.deleteAccount(accountNumber, authenticationHolder.getUserId());
  }
}
