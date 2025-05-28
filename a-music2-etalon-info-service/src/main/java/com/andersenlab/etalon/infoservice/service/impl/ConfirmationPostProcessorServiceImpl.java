package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.util.Constants.AUTHENTICATED_USER_ID;
import static com.andersenlab.etalon.infoservice.util.enums.Operation.CREATE_PAYMENT;
import static com.andersenlab.etalon.infoservice.util.enums.Operation.CREATE_TRANSFER;
import static com.andersenlab.etalon.infoservice.util.enums.Operation.OPEN_DEPOSIT;

import com.andersenlab.etalon.infoservice.dto.sqs.CreateConfirmationMessage;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.ConfirmationPostProcessorService;
import com.andersenlab.etalon.infoservice.sqs.SqsMessageProducer;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationPostProcessorServiceImpl implements ConfirmationPostProcessorService {
  private Map<Operation, ConfirmationPostProcessor> strategies;
  private final SqsMessageProducer sqsMessageProducer;
  private final AuthenticationHolder authenticationHolder;

  @Value("${sqs.queue.create-confirmation.name}")
  private String createConfirmationQueue;

  @PostConstruct
  protected void init() {
    strategies = new HashMap<>();
    strategies.put(
        CREATE_PAYMENT,
        (payload, header) ->
            sqsMessageProducer.sendMessage(createConfirmationQueue, payload, header));
    strategies.put(
        CREATE_TRANSFER,
        (payload, header) ->
            sqsMessageProducer.sendMessage(createConfirmationQueue, payload, header));
    strategies.put(
        OPEN_DEPOSIT,
        (payload, header) ->
            sqsMessageProducer.sendMessage(createConfirmationQueue, payload, header));
    strategies.put(
        Operation.USER_REGISTRATION,
        (payload, header) ->
            sqsMessageProducer.sendMessage(createConfirmationQueue, payload, header));
    strategies.put(
        Operation.EMAIL_MODIFICATION,
        (payload, header) ->
            sqsMessageProducer.sendMessage(createConfirmationQueue, payload, header));
  }

  public void postProcess(ConfirmationEntity confirmationEntity) {
    if (Objects.nonNull(confirmationEntity) && Objects.nonNull(confirmationEntity.getOperation())) {
      Operation operation = confirmationEntity.getOperation();
      if (strategies.containsKey(operation)) {
        String userId =
            Objects.nonNull(authenticationHolder.getUserId())
                ? authenticationHolder.getUserId()
                : "no auth";
        Map<String, Object> header = Map.of(AUTHENTICATED_USER_ID, userId);
        CreateConfirmationMessage payload =
            CreateConfirmationMessage.builder().confirmationId(confirmationEntity.getId()).build();
        strategies.get(operation).process(payload, header);
      }
    }
  }

  private interface ConfirmationPostProcessor {
    void process(CreateConfirmationMessage payload, Map<String, Object> header);
  }
}
