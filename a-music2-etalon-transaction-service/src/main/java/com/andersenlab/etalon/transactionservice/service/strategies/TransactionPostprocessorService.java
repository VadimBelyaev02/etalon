package com.andersenlab.etalon.transactionservice.service.strategies;

import com.andersenlab.etalon.transactionservice.entity.PaymentEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.entity.TransferEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.repository.PaymentRepository;
import com.andersenlab.etalon.transactionservice.repository.TransferRepository;
import com.andersenlab.etalon.transactionservice.sqs.SqsMessageProducer;
import com.andersenlab.etalon.transactionservice.util.Constants;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionPostprocessorService {
  private Map<Details, TransactionPostprocessor> strategies;

  private final TransferRepository transferRepository;
  private final PaymentRepository paymentRepository;
  private final SqsMessageProducer sqsMessageProducer;
  private final AuthenticationHolder authenticationHolder;

  @Value("${sqs.queue.open-deposit.name}")
  private String openDepositQueue;

  @PostConstruct
  protected void init() {
    strategies = new HashMap<>();

    strategies.put(
        Details.OPEN_DEPOSIT,
        transactionEntity -> {
          log.info(
              "{openDeposit} message -> {} to be send on queue -> {}",
              transactionEntity.getId(),
              openDepositQueue);
          sqsMessageProducer.send(
              openDepositQueue,
              transactionEntity.getId(),
              Map.of(
                  Constants.AUTHENTICATED_USER_ID,
                  Optional.ofNullable(authenticationHolder.getUserId())
                      .orElseThrow(
                          () ->
                              new BusinessException(
                                  HttpStatus.UNAUTHORIZED,
                                  BusinessException.NO_ANY_USER_AUTHORIZED_FOUND))));
        });

    strategies.put(
        Details.TRANSFER,
        transactionEntity -> {
          TransferEntity transferEntity =
              transferRepository
                  .findByTransactionId(transactionEntity.getId())
                  .orElseThrow(
                      () ->
                          new BusinessException(
                              HttpStatus.NOT_FOUND,
                              BusinessException.TRANSFER_NOT_FOUND_BY_ID.formatted(
                                  transactionEntity.getId())));
          transferEntity.setStatus(
              TransactionStatus.APPROVED.equals(transactionEntity.getStatus())
                  ? TransferStatus.APPROVED
                  : TransferStatus.DECLINED);
          transferRepository.save(transferEntity);
          log.info(
              "Transfer with id-{} to be changed on status -> {}",
              transferEntity.getId(),
              transactionEntity.getStatus());
        });

    strategies.put(
        Details.PAYMENT,
        transactionEntity -> {
          PaymentEntity payment =
              paymentRepository
                  .findByTransactionId(transactionEntity.getId())
                  .orElseThrow(
                      () ->
                          new BusinessException(
                              HttpStatus.NOT_FOUND,
                              BusinessException.PAYMENT_NOT_FOUND_BY_ID.formatted(
                                  transactionEntity.getId())));
          payment.setStatus(
              TransactionStatus.APPROVED.equals(transactionEntity.getStatus())
                  ? PaymentStatus.APPROVED
                  : PaymentStatus.DECLINED);
          paymentRepository.save(payment);
          log.info(
              "Payment with id-{} to be changed on status -> {}",
              payment.getId(),
              payment.getStatus());
        });
  }

  public void postProcess(TransactionEntity transactionEntity) {
    if (Objects.nonNull(transactionEntity) && Objects.nonNull(transactionEntity.getDetails())) {
      Details details = transactionEntity.getDetails();
      if (strategies.containsKey(details)) {
        strategies.get(details).process(transactionEntity);
      }
    }
  }

  private interface TransactionPostprocessor {
    void process(TransactionEntity transactionEntity);
  }
}
