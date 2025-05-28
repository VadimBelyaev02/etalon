package com.andersenlab.etalon.infoservice.client;

import com.andersenlab.etalon.infoservice.config.FeignConfig;
import com.andersenlab.etalon.infoservice.dto.response.EventResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.TransferResponseDto;
import com.andersenlab.etalon.infoservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.infoservice.util.enums.TransferStatus;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "transaction-service",
    url = "${feign.transaction-service.url}",
    configuration = FeignConfig.class,
    path = "/transaction")
public interface TransactionServiceClient {
  String API_V1_URI = "/api/v1";
  String API_V2_URI = "/api/v2";
  String TRANSACTION_URI = "/transactions";
  String TRANSACTION_ID_PATH = "/{transactionId}";
  String TRANSACTIONS_URL = API_V1_URI + TRANSACTION_URI;
  String TRANSACTION_BY_ID_URL = TRANSACTIONS_URL + TRANSACTION_ID_PATH;
  String TRANSFERS_URI = "/transfers";
  String TRANSFER_ID_URI = "/{transferId}";
  String TRANSFER_URL = API_V1_URI + TRANSFERS_URI + TRANSFER_ID_URI;
  String PAYMENT_URI = "/payments";
  String PAYMENT_ID_URI = "/{paymentId}";
  String CONFIRM_URI = "/confirm";
  String PAYMENT_CONFIRM_URL = API_V1_URI + PAYMENT_URI + PAYMENT_ID_URI + CONFIRM_URI;
  String CONFIRMED_URL = "/confirmed";
  String TRANSFER_CONFIRMED_V2_URL = API_V2_URI + TRANSFERS_URI + TRANSFER_ID_URI + CONFIRMED_URL;
  String STATUS_URI = "/status";
  String PAYMENT_STATUS_UPDATE_URL = API_V1_URI + PAYMENT_URI + PAYMENT_ID_URI + STATUS_URI;
  String TRANSFER_STATUS_UPDATE_URL = API_V1_URI + TRANSFERS_URI + TRANSFER_ID_URI + STATUS_URI;

  @GetMapping(TRANSACTION_BY_ID_URL)
  TransactionDetailedResponseDto getDetailedTransaction(
      @PathVariable Long transactionId, @RequestHeader("authenticated-user-id") String userId);

  @GetMapping(TRANSACTIONS_URL)
  List<EventResponseDto> getAllTransactions(
      @SpringQueryMap TransactionFilter filter,
      @RequestHeader("authenticated-user-id") String userId);

  @GetMapping(TRANSFER_URL)
  TransferResponseDto getTransferById(
      @PathVariable final long transferId, @RequestHeader("authenticated-user-id") String userId);

  @PostMapping(PAYMENT_CONFIRM_URL)
  MessageResponseDto processCreatingPayment(@PathVariable final Long paymentId);

  @PostMapping(TRANSFER_CONFIRMED_V2_URL)
  MessageResponseDto processConfirmedTransfer(@PathVariable final Long transferId);

  @PutMapping(TRANSFER_STATUS_UPDATE_URL)
  void confirmTransferStatus(
      @PathVariable("transferId") Long transferId, @RequestParam("status") TransferStatus status);

  @PutMapping(PAYMENT_STATUS_UPDATE_URL)
  void confirmPaymentStatus(
      @PathVariable("paymentId") Long paymentId,
      @RequestParam("status") PaymentStatus paymentStatus);
}
