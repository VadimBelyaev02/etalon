package com.andersenlab.etalon.accountservice.service;

import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.RequestOptionDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInfoResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import java.util.List;

public interface AccountService {
  AccountResponseDto createAccount(AccountCreationRequestDto accountCreation);

  void updateAccount(Long id, AccountRequestDto accountRequestDto);

  void deleteAccount(String accountNumber, String userId);

  AccountBalanceResponseDto getAccountBalance(String accountNumber, String userId);

  AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber, String userId);

  AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber);

  void replenishAccountBalance(
      String accountNumber,
      AccountReplenishByAccountNumberRequestDto accountReplenishByIdRequestDto);

  void withdrawAccountBalance(
      String accountNumber, AccountWithdrawByAccountNumberRequestDto accountWithdrawByIdRequestDto);

  List<AccountNumberResponseDto> getAllAccountNumbers(String userId);

  AccountInfoResponseDto getAccountInfoBySelectedOption(RequestOptionDto options);

  List<AccountInterestResponseDto> getAccountsBalances(List<String> accountsNumbers, String userId);

  Boolean changeIsBlocked(String accountNumber, String userId);
}
