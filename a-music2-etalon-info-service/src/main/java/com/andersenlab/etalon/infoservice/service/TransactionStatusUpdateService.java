package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.client.TransactionServiceClient;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import com.andersenlab.etalon.infoservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.infoservice.util.enums.TransferStatus;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionStatusUpdateService {
  private final TransactionServiceClient transactionServiceClient;

  private final Map<Operation, TransactionStatusUpdateStrategy> statusUpdateStrategies =
      new EnumMap<>(Operation.class);

  interface TransactionStatusUpdateStrategy {
    void updateStatusToCodeConfirmed(ConfirmationEntity confirmation);
  }

  @PostConstruct
  private void init() {
    statusUpdateStrategies.put(
        Operation.CREATE_TRANSFER,
        confirmation -> {
          log.info(
              "Updating transfer status to CODE_CONFIRMED for transfer id {}",
              confirmation.getTargetId());
          transactionServiceClient.confirmTransferStatus(
              confirmation.getTargetId(), TransferStatus.CODE_CONFIRMED);
        });

    statusUpdateStrategies.put(
        Operation.CREATE_PAYMENT,
        confirmation -> {
          log.info(
              "Updating payment status to CODE_CONFIRMED for payment id {}",
              confirmation.getTargetId());
          transactionServiceClient.confirmPaymentStatus(
              confirmation.getTargetId(), PaymentStatus.CODE_CONFIRMED);
        });
  }

  public void updateStatusToCodeConfirmed(ConfirmationEntity confirmation) {
    TransactionStatusUpdateStrategy strategy =
        statusUpdateStrategies.get(confirmation.getOperation());

    if (Objects.isNull(strategy)) {
      log.info(
          "Operation {} is not handled by TransactionStatusUpdateService",
          confirmation.getOperation());
      return;
    }

    strategy.updateStatusToCodeConfirmed(confirmation);
  }
}
