package com.andersenlab.etalon.depositservice.client.service;

import com.andersenlab.etalon.depositservice.client.AccountServiceClient;
import com.andersenlab.etalon.depositservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class AccountService {
  private final AccountServiceClient accountServiceClient;

  public AccountBalanceResponseDto getAccountBalanceByAccountNumber(String accountNumber) {
    return accountServiceClient.getAccountBalanceByAccountNumber(accountNumber);
  }

  public AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber) {
    return accountServiceClient.getDetailedAccountInfo(accountNumber);
  }

  public List<AccountInterestResponseDto> getAccountsBalances(List<String> accountsNumbers) {
    return accountServiceClient.getAccountsBalances(accountsNumbers);
  }

  public AccountResponseDto createAccount(AccountCreationRequestDto accountCreation) {
    return accountServiceClient.createAccount(accountCreation);
  }

  public void deleteAccount(@PathVariable String accountNumber) {
    accountServiceClient.deleteAccount(accountNumber);
  }
}
