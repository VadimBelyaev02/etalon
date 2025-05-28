package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import java.util.List;

public interface AccountService {
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(String accountNumber);

  AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber);

  MessageResponseDto replenishAccountBalance(
      String accountNumber,
      final AccountReplenishByAccountNumberRequestDto accountReplenishByAccountNumberRequestDto);

  MessageResponseDto withdrawAccountBalance(
      String accountNumber,
      final AccountWithdrawByAccountNumberRequestDto accountWithdrawByAccountNumberRequestDto);

  List<AccountNumberResponseDto> getAllAccountNumbers(String userId);

  List<String> getUserAccountNumbers(String userId);
}
