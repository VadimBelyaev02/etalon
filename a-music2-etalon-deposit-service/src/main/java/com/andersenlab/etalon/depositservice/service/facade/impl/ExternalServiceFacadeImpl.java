package com.andersenlab.etalon.depositservice.service.facade.impl;

import com.andersenlab.etalon.depositservice.client.service.AccountService;
import com.andersenlab.etalon.depositservice.client.service.InfoService;
import com.andersenlab.etalon.depositservice.client.service.TransactionService;
import com.andersenlab.etalon.depositservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.depositservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.depositservice.dto.auth.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionInfoResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.depositservice.service.facade.ExternalServiceFacade;
import com.andersenlab.etalon.depositservice.util.enums.Details;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalServiceFacadeImpl implements ExternalServiceFacade {
  private final AccountService accountService;
  private final InfoService infoService;
  private final TransactionService transactionService;

  @Override
  public AccountBalanceResponseDto getAccountBalanceByAccountNumber(String accountNumber) {
    return accountService.getAccountBalanceByAccountNumber(accountNumber);
  }

  @Override
  public AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber) {
    return accountService.getDetailedAccountInfo(accountNumber);
  }

  @Override
  public TransactionMessageResponseDto createTransaction(
      TransactionCreateRequestDto transactionCreateRequestDto) {
    return transactionService.createTransaction(transactionCreateRequestDto);
  }

  @Override
  public TransactionMessageResponseDto createTransactionForDeposit(
      String replenishAccount,
      BigDecimal transactionAmount,
      String withdrawalAccount,
      String transactionName,
      Details details) {
    return transactionService.createTransactionForDeposit(
        replenishAccount, transactionAmount, withdrawalAccount, transactionName, details);
  }

  @Override
  public CreateConfirmationResponseDto createConfirmation(
      CreateConfirmationRequestDto authConfirmation) {
    return infoService.createConfirmation(authConfirmation);
  }

  @Override
  public List<AccountInterestResponseDto> getAccountsBalances(List<String> accountsNumbers) {
    return accountService.getAccountsBalances(accountsNumbers);
  }

  @Override
  public List<EventResponseDto> getAllTransactionsForAccounts(List<String> accountNumber) {
    return transactionService.getAllTransactionsForAccounts(accountNumber);
  }

  @Override
  public TransactionInfoResponseDto getDetailedTransaction(Long transactionId) {
    return transactionService.getDetailedTransaction(transactionId);
  }

  @Override
  public void deleteAccount(String accountNumber) {
    accountService.deleteAccount(accountNumber);
  }

  @Override
  public AccountResponseDto createAccount(AccountCreationRequestDto accountCreation) {
    return accountService.createAccount(accountCreation);
  }
}
