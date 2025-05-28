package com.andersenlab.etalon.transactionservice.sqs;

import static com.andersenlab.etalon.transactionservice.util.Constants.AUTHENTICATED_USER_ID;

import com.andersenlab.etalon.transactionservice.dto.sqs.TransactionQueueMessage;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.service.TransactionService;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
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
  private final TransactionService transactionService;
  private final AuthenticationHolder authenticationHolder;

  @SqsListener("${sqs.queue.create-transaction.name}")
  public void consumeCreatedTransaction(
      @Payload TransactionQueueMessage message, @Headers Map<String, String> headers) {
    log.info("{consumeCreatedTransaction} message received -> {}", message);
    String authenticatedUserId = headers.get(AUTHENTICATED_USER_ID);
    authenticationHolder.setUserId(authenticatedUserId);
    log.info(
        "{consumeCreatedTransaction} message received -> payload {}  and header {}",
        message,
        authenticatedUserId);
    transactionService.processTransaction(
        message.transactionId(), message.loanInterestAmount(), message.loanPenaltyAmount());
    log.info(
        "{consumeCreatedTransaction} -> processing transaction with id -> {} and interest amount -> {} and penalty amount -> {}",
        message.transactionId(),
        message.loanInterestAmount(),
        message.loanPenaltyAmount());
  }

  @SqsListener("${sqs.queue.create-transaction-dlq.name}")
  public void consumeCreatedTransactionFromDlq(@Payload TransactionQueueMessage message) {
    log.info("{consumeCreatedTransactionFromDlq} message received -> {}", message);
    TransactionEntity transaction = transactionService.getTransactionById(message.transactionId());
    transactionService.changeTransactionStatusAndProcess(TransactionStatus.DECLINED, transaction);
    log.info(
        "{consumeCreatedTransactionFromDlq} an error occurred while message consumption, transaction with id -> {} has declined",
        message.transactionId());
  }

  @MessageExceptionHandler
  public void handleQueueException(RuntimeException exception) {
    log.error("{handleQueueException} Error occurred: " + exception.getMessage());
  }
}
