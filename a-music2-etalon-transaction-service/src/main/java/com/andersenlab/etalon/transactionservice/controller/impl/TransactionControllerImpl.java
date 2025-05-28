package com.andersenlab.etalon.transactionservice.controller.impl;

import com.andersenlab.etalon.transactionservice.controller.TransactionController;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CustomPageDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.TransactionService;
import com.andersenlab.etalon.transactionservice.util.CustomPageDtoUtils;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping
@RestController
@RequiredArgsConstructor
@Tag(name = "Transaction")
public class TransactionControllerImpl implements TransactionController {

  private final TransactionService transactionService;
  private final AuthenticationHolder authenticationHolder;

  @PostMapping(TRANSACTIONS_V1_URL)
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionMessageResponseDto createTransaction(
      @RequestBody TransactionCreateRequestDto transactionCreateRequestDto) {
    log.info(
        "{createTransaction} -> Creating transaction for user {}",
        authenticationHolder.getUserId());

    return transactionService.createTransaction(transactionCreateRequestDto);
  }

  @Override
  @GetMapping(TRANSACTIONS_V1_URL)
  public List<EventResponseDto> getAllTransactions(@ParameterObject TransactionFilter filter) {
    return transactionService.getAllUserTransactions(authenticationHolder.getUserId(), filter);
  }

  @GetMapping(TRANSACTIONS_V1_URL + ACCOUNTS_URI)
  public List<EventResponseDto> getTransactionsForAccounts(
      @RequestParam List<String> accountNumber) {
    return transactionService.getTimeframeTransactionsForAccounts(accountNumber);
  }

  @Override
  @GetMapping(TRANSACTIONS_V1_URL + TRANSACTION_ID_PATH)
  public TransactionDetailedResponseDto getDetailedTransaction(@PathVariable Long transactionId) {
    return transactionService.getDetailedTransaction(
        authenticationHolder.getUserId(), transactionId);
  }

  @Override
  @GetMapping(TRANSACTIONS_V2_URL)
  public CustomPageDto<TransactionExtendedResponseDto> getAllExtendedTransactions(
      @ParameterObject TransactionFilter filter) {
    var transactionsExtended =
        transactionService.getAllUserTransactionsExtended(authenticationHolder.getUserId(), filter);
    return CustomPageDtoUtils.toCustomPageDto(transactionsExtended);
  }
}
