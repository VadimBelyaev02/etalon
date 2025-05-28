package com.andersenlab.etalon.depositservice.sqs;

import com.andersenlab.etalon.depositservice.dto.transaction.response.TransactionInfoResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositOrderEntity;
import com.andersenlab.etalon.depositservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.depositservice.service.business.DepositOrderService;
import com.andersenlab.etalon.depositservice.service.dao.DepositOrderServiceDao;
import com.andersenlab.etalon.depositservice.service.facade.ExternalServiceFacade;
import com.andersenlab.etalon.depositservice.util.Constants;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SqsMessageListener {

  private final DepositOrderService depositOrderService;
  private final DepositOrderServiceDao depositOrderServiceDao;
  private final AuthenticationHolder authenticationHolder;
  private final ExternalServiceFacade externalServiceFacade;

  @SqsListener("${sqs.queue.open-deposit.name}")
  public void consumeOpenDeposit(
      @Payload long transactionId, @Headers Map<String, String> headers) {
    log.info("{consumeOpenDeposit} -> Consuming message with transactionId {}", transactionId);
    String authenticatedUserId = headers.get(Constants.AUTHENTICATED_USER_ID);
    log.info("{consumeOpenDeposit} -> Consuming message with userId -> {}", authenticatedUserId);
    authenticationHolder.setUserId(authenticatedUserId);
    depositOrderService.proceedOpeningNewDeposit(transactionId);
  }

  @SqsListener("${sqs.queue.open-deposit-dlq.name}")
  public void consumeOpenDepositDlq(@Payload long transactionId) {
    log.info(
        "{consumeOpenDepositDlq} an error occurred during message receiving, message '{}' sent to DLQ",
        transactionId);
    DepositOrderEntity depositOrder =
        depositOrderServiceDao.findDepositOrderEntityByTransactionId(transactionId);
    depositOrder.setStatus(DepositStatus.REJECTED);
    depositOrderServiceDao.save(depositOrder);
    TransactionInfoResponseDto transactionInfoResponseDto =
        externalServiceFacade.getDetailedTransaction(transactionId);
    externalServiceFacade.deleteAccount(transactionInfoResponseDto.incomeAccountNumber());
    log.info("{consumeFromDlq} -> an error occurred while message consuming. Deposit was rejected");
  }

  @MessageExceptionHandler
  public void handleQueueException(RuntimeException exception) {
    log.error("{handleQueueException} Error occurred: {}", exception.getMessage());
  }
}
