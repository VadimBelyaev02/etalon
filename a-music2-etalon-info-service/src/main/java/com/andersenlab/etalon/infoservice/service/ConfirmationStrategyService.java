package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.client.DepositServiceClient;
import com.andersenlab.etalon.infoservice.client.TransactionServiceClient;
import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.PersonalInfoDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.exception.TechnicalException;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationStrategyService {
  private final DepositServiceClient depositServiceClient;
  private final TransactionServiceClient transactionServiceClient;
  private final UserServiceClient userServiceClient;

  private final Map<Operation, ConfirmationStrategy> operationConfirmationStrategies =
      new EnumMap<>(Operation.class);

  interface ConfirmationStrategy {
    MessageResponseDto confirm(ConfirmationEntity confirmation);
  }

  @PostConstruct
  private void init() {
    operationConfirmationStrategies.put(
        Operation.OPEN_DEPOSIT,
        confirmation -> depositServiceClient.processOpeningNewDeposit(confirmation.getTargetId()));

    operationConfirmationStrategies.put(
        Operation.CREATE_PAYMENT,
        confirmation ->
            transactionServiceClient.processCreatingPayment(confirmation.getTargetId()));

    operationConfirmationStrategies.put(
        Operation.CREATE_TRANSFER,
        confirmation ->
            transactionServiceClient.processConfirmedTransfer(confirmation.getTargetId()));

    operationConfirmationStrategies.put(
        Operation.USER_REGISTRATION,
        confirmation -> {
          PersonalInfoDto responseDto =
              userServiceClient.processUserRegistration(confirmation.getTargetId());
          return new MessageResponseDto("Ok", responseDto);
        });

    operationConfirmationStrategies.put(
        Operation.EMAIL_MODIFICATION,
        confirmation -> userServiceClient.processEmailModification(confirmation.getTargetId()));
  }

  public MessageResponseDto confirm(ConfirmationEntity confirmation) {
    ConfirmationStrategy confirmationStrategy =
        operationConfirmationStrategies.get(confirmation.getOperation());

    if (Objects.isNull(confirmationStrategy)) {
      throw new TechnicalException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          String.format(
              TechnicalException.NO_CONFIRMATION_STRATEGY_OPERATION, confirmation.getOperation()));
    }
    return confirmationStrategy.confirm(confirmation);
  }
}
