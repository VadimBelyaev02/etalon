package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;

public interface TransactionService {

  TransactionMessageResponseDto createTransaction(
      TransactionCreateRequestDto transactionCreateRequestDto);

  void processTransaction(
      long transactionId, BigDecimal loanInterestAmount, BigDecimal loanPenaltyAmount);

  List<EventResponseDto> getAllUserTransactions(String userId, TransactionFilter filter);

  Page<TransactionExtendedResponseDto> getAllUserTransactionsExtended(
      String userId, TransactionFilter filter);

  List<EventResponseDto> getTimeframeTransactionsForAccounts(List<String> accountsNumbers);

  TransactionDetailedResponseDto getDetailedTransaction(String userId, Long transactionId);

  TransactionEntity changeTransactionStatusAndProcess(
      TransactionStatus status, TransactionEntity transactionEntity);

  TransactionEntity getTransactionById(long id);
}
