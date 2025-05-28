package com.andersenlab.etalon.depositservice.client;

import com.andersenlab.etalon.depositservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.depositservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionInfoResponseDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionMessageResponseDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "transaction-service",
    url = "${feign.transaction-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/transaction")
public interface TransactionServiceClient {
  String API_V1_URI = "/api/v1";
  String TRANSACTION_URI = "/transactions";
  String ACCOUNT_URI = "/accounts";
  String TRANSACTION_ID_PATH = "/{transactionId}";
  String TRANSACTION_BY_ACCOUNT_NUMBER_URL = API_V1_URI + TRANSACTION_URI + ACCOUNT_URI;
  String TRANSACTIONS_URL = API_V1_URI + TRANSACTION_URI;
  String TRANSACTION_BY_TRANSACTION_ID_URL = TRANSACTIONS_URL + TRANSACTION_ID_PATH;

  @GetMapping(TRANSACTION_BY_ACCOUNT_NUMBER_URL)
  List<EventResponseDto> getAllTransactionsForAccounts(@RequestParam List<String> accountNumber);

  @PostMapping(TRANSACTIONS_URL)
  TransactionMessageResponseDto createTransaction(
      @RequestBody TransactionCreateRequestDto transactionCreateRequestDto);

  @GetMapping(TRANSACTION_BY_TRANSACTION_ID_URL)
  TransactionInfoResponseDto getDetailedTransaction(@PathVariable Long transactionId);
}
