package com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation;

import com.andersenlab.etalon.infoservice.client.UserServiceClient;
import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation.CreatePaymentRequestStrategy;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation.CreateTransferRequestStrategy;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation.EmailModificationRequestStrategy;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation.OpenDepositRequestStrategy;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.operation.UserRegistrationRequestStrategy;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class EmailRequestPreparationContext {
  private final Map<Operation, EmailRequestPreparationStrategy> strategies;

  public EmailRequestPreparationContext(
      UserServiceClient userServiceClient, AuthenticationHolder authenticationHolder) {
    this.strategies = new HashMap<>();
    this.strategies.put(
        Operation.EMAIL_MODIFICATION, new EmailModificationRequestStrategy(userServiceClient));
    this.strategies.put(
        Operation.USER_REGISTRATION, new UserRegistrationRequestStrategy(userServiceClient));
    this.strategies.put(
        Operation.OPEN_DEPOSIT,
        new OpenDepositRequestStrategy(userServiceClient, authenticationHolder));
    this.strategies.put(
        Operation.CREATE_PAYMENT,
        new CreatePaymentRequestStrategy(userServiceClient, authenticationHolder));
    this.strategies.put(
        Operation.CREATE_TRANSFER,
        new CreateTransferRequestStrategy(userServiceClient, authenticationHolder));
  }

  public BaseEmailRequestDto prepareEmail(ConfirmationEntity confirmation) {
    EmailRequestPreparationStrategy strategy = strategies.get(confirmation.getOperation());
    if (strategy == null) {
      throw new IllegalArgumentException(
          "No strategy found for operation: " + confirmation.getOperation());
    }
    return strategy.prepareEmailRequest(confirmation);
  }
}
