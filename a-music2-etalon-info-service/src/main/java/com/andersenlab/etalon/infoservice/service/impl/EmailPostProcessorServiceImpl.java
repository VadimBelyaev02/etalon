package com.andersenlab.etalon.infoservice.service.impl;

import static com.andersenlab.etalon.infoservice.util.Constants.AUTHENTICATED_USER_ID;
import static com.andersenlab.etalon.infoservice.util.enums.EmailType.CONFIRMATION;
import static com.andersenlab.etalon.infoservice.util.enums.EmailType.CONFIRMATION_STATEMENT;
import static com.andersenlab.etalon.infoservice.util.enums.EmailType.EMAIL_MODIFY_CONFIRMATION;
import static com.andersenlab.etalon.infoservice.util.enums.EmailType.PASSWORD_RESET_CONFIRMATION;
import static com.andersenlab.etalon.infoservice.util.enums.EmailType.REGISTRATION;
import static com.andersenlab.etalon.infoservice.util.enums.EmailType.SHARING_TRANSFER_RECEIPT;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.EmailPostProcessorService;
import com.andersenlab.etalon.infoservice.sqs.SqsMessageProducer;
import com.andersenlab.etalon.infoservice.util.enums.EmailType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailPostProcessorServiceImpl implements EmailPostProcessorService {

  private final SqsMessageProducer sqsMessageProducer;
  private final AuthenticationHolder authenticationHolder;
  private Map<EmailType, MessageSenderPostProcessor> strategies;

  @Value("${sqs.queue.send-email.name}")
  private String messageSenderQueue;

  @PostConstruct
  public void init() {
    strategies = new HashMap<>();
    strategies.put(
        CONFIRMATION,
        (payload, header) -> sqsMessageProducer.sendMessage(messageSenderQueue, payload));
    strategies.put(
        REGISTRATION,
        (payload, header) -> sqsMessageProducer.sendMessage(messageSenderQueue, payload));
    strategies.put(
        EMAIL_MODIFY_CONFIRMATION,
        (payload, header) -> sqsMessageProducer.sendMessage(messageSenderQueue, payload));
    strategies.put(
        PASSWORD_RESET_CONFIRMATION,
        (payload, header) -> sqsMessageProducer.sendMessage(messageSenderQueue, payload));
    strategies.put(
        SHARING_TRANSFER_RECEIPT,
        (payload, header) -> sqsMessageProducer.sendMessage(messageSenderQueue, payload, header));
    strategies.put(
        CONFIRMATION_STATEMENT,
        (payload, header) -> sqsMessageProducer.sendMessage(messageSenderQueue, payload, header));
  }

  @Override
  public void postProcess(BaseEmailRequestDto requestDto) {
    String userId =
        Objects.nonNull(authenticationHolder.getUserId())
            ? authenticationHolder.getUserId()
            : "no auth";
    Map<String, Object> header = Map.of(AUTHENTICATED_USER_ID, userId);
    strategies.get(requestDto.getType()).process(requestDto, header);
  }

  public interface MessageSenderPostProcessor {
    void process(BaseEmailRequestDto payload, Map<String, Object> header);
  }
}
