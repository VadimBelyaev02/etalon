package com.andersenlab.etalon.loanservice.client;

import com.andersenlab.etalon.loanservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.transaction.request.TransactionCreateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "transaction-service",
    url = "${feign.transaction-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/transaction")
public interface TransactionServiceClient {

  String API_V1_URI = "/api/v1";
  String TRANSACTION_URI = "/transactions";

  String TRANSACTIONS_URL = API_V1_URI + TRANSACTION_URI;

  @PostMapping(TRANSACTIONS_URL)
  MessageResponseDto createTransaction(
      @RequestBody TransactionCreateRequestDto transactionCreateRequestDto);
}
