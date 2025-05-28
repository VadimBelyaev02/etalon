package com.andersenlab.etalon.depositservice.service.facade;

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
import com.andersenlab.etalon.depositservice.util.enums.Details;
import java.math.BigDecimal;
import java.util.List;

public interface ExternalServiceFacade {
  AccountBalanceResponseDto getAccountBalanceByAccountNumber(String accountNumber);

  AccountDetailedResponseDto getDetailedAccountInfo(String accountNumber);

  TransactionMessageResponseDto createTransaction(
      TransactionCreateRequestDto transactionCreateRequestDto);

  TransactionMessageResponseDto createTransactionForDeposit(
      String replenishAccount,
      BigDecimal transactionAmount,
      String withdrawalAccount,
      String transactionName,
      Details details);

  CreateConfirmationResponseDto createConfirmation(CreateConfirmationRequestDto authConfirmation);

  List<AccountInterestResponseDto> getAccountsBalances(List<String> accountsNumbers);

  List<EventResponseDto> getAllTransactionsForAccounts(List<String> accountNumber);

  TransactionInfoResponseDto getDetailedTransaction(Long transactionId);

  void deleteAccount(String accountNumber);

  AccountResponseDto createAccount(AccountCreationRequestDto accountCreation);
}
