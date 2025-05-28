package com.andersenlab.etalon.depositservice.client.service;

import com.andersenlab.etalon.depositservice.client.TransactionServiceClient;
import com.andersenlab.etalon.depositservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionInfoResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.depositservice.util.enums.Details;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {
  private final TransactionServiceClient transactionServiceClient;

  public List<EventResponseDto> getAllTransactionsForAccounts(List<String> accountNumber) {
    return transactionServiceClient.getAllTransactionsForAccounts(accountNumber);
  }

  public TransactionMessageResponseDto createTransaction(
      TransactionCreateRequestDto transactionCreateRequestDto) {
    return transactionServiceClient.createTransaction(transactionCreateRequestDto);
  }

  public TransactionInfoResponseDto getDetailedTransaction(Long transactionId) {
    return transactionServiceClient.getDetailedTransaction(transactionId);
  }

  public TransactionMessageResponseDto createTransactionForDeposit(
      String replenishAccount,
      BigDecimal transactionAmount,
      String withdrawalAccount,
      String transactionName,
      Details details) {
    return createTransaction(
        TransactionCreateRequestDto.builder()
            .accountNumberReplenished(replenishAccount)
            .accountNumberWithdrawn(withdrawalAccount)
            .amount(transactionAmount)
            .details(details)
            .feeAmount(BigDecimal.ZERO)
            .isFeeProvided(false)
            .loanInterestAmount(BigDecimal.ZERO)
            .transactionName(transactionName)
            .build());
  }
}
