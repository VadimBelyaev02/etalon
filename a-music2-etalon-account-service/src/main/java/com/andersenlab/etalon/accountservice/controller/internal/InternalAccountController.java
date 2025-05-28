package com.andersenlab.etalon.accountservice.controller.internal;

import com.andersenlab.etalon.accountservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(InternalAccountController.URI)
public class InternalAccountController {

  public static final String URI = "/internal/api/v1";
  private static final String ACCOUNTS_URI = "/accounts";
  private static final String WITHDRAWN_URI = "/withdrawn";
  private static final String REPLENISHED_URI = "/replenished";
  private static final String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  private static final String ACCOUNTS_BY_NUMBER_URI = "/accounts-by-number";
  private static final String ACCOUNT_V1_ACCOUNT_BY_NUMBER_URL =
      ACCOUNTS_BY_NUMBER_URI + ACCOUNT_NUMBER_PATH;
  public static final String ACCOUNT_V1_REPLENISHED_URL =
      ACCOUNTS_URI + ACCOUNT_NUMBER_PATH + REPLENISHED_URI;
  public static final String ACCOUNT_V1_WITHDRAWN_URL =
      ACCOUNTS_URI + ACCOUNT_NUMBER_PATH + WITHDRAWN_URI;
  private final AccountService accountService;

  @GetMapping(ACCOUNT_V1_ACCOUNT_BY_NUMBER_URL)
  public AccountDetailedResponseDto getDetailedAccountInfo(@PathVariable String accountNumber) {
    return accountService.getDetailedAccountInfo(accountNumber);
  }

  @PostMapping(ACCOUNT_V1_REPLENISHED_URL)
  public MessageResponseDto replenishAccountBalance(
      @PathVariable String accountNumber,
      @RequestBody final AccountReplenishByAccountNumberRequestDto accountReplenishByIdRequestDto) {
    accountService.replenishAccountBalance(accountNumber, accountReplenishByIdRequestDto);
    return new MessageResponseDto(MessageResponseDto.ACCOUNT_BALANCE_REPLENISHED_SUCCESSFULLY);
  }

  @PostMapping(ACCOUNT_V1_WITHDRAWN_URL)
  public MessageResponseDto withdrawAccountBalance(
      @PathVariable String accountNumber,
      @RequestBody final AccountWithdrawByAccountNumberRequestDto accountWithdrawByIdRequestDto) {
    accountService.withdrawAccountBalance(accountNumber, accountWithdrawByIdRequestDto);
    return new MessageResponseDto(MessageResponseDto.ACCOUNT_BALANCE_WITHDRAWN_SUCCESSFULLY);
  }
}
