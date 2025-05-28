package com.andersenlab.etalon.transactionservice.service.impl;

import com.andersenlab.etalon.transactionservice.client.AccountServiceClient;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
  private final AccountServiceClient accountServiceClient;

  @Override
  public AccountBalanceResponseDto getAccountBalanceByAccountNumber(String accountNumber) {
    return accountServiceClient.getAccountBalanceByAccountNumber(accountNumber);
  }

  @Override
  public AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber) {
    return accountServiceClient.getDetailedAccountInfoInternalCall(accountNumber);
  }

  @Override
  public MessageResponseDto replenishAccountBalance(
      String accountNumber,
      AccountReplenishByAccountNumberRequestDto accountReplenishByAccountNumberRequestDto) {
    return accountServiceClient.replenishAccountBalance(
        accountNumber, accountReplenishByAccountNumberRequestDto);
  }

  @Override
  public MessageResponseDto withdrawAccountBalance(
      String accountNumber,
      AccountWithdrawByAccountNumberRequestDto accountWithdrawByAccountNumberRequestDto) {
    return accountServiceClient.withdrawAccountBalance(
        accountNumber, accountWithdrawByAccountNumberRequestDto);
  }

  @Override
  public List<AccountNumberResponseDto> getAllAccountNumbers(String userId) {
    return accountServiceClient.getAllAccountNumbers(userId);
  }

  @Override
  public List<String> getUserAccountNumbers(String userId) {
    return getAllAccountNumbers(userId).stream()
        .map(AccountNumberResponseDto::accountNumber)
        .toList();
  }
}
