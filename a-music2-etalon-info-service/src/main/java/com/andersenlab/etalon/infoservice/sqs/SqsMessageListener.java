package com.andersenlab.etalon.infoservice.sqs;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.sqs.CreateConfirmationMessage;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.service.ConfirmationService;
import com.andersenlab.etalon.infoservice.service.EmailService;
import com.andersenlab.etalon.infoservice.util.Constants;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableSqs
public class SqsMessageListener {
  private final ConfirmationService confirmationService;
  private final EmailService emailService;
  private final AuthenticationHolder authenticationHolder;

  @SqsListener("${sqs.queue.create-confirmation.name}")
  public void consumeCreateConfirmation(
      @Payload CreateConfirmationMessage message, @Headers Map<String, String> headers) {
    log.info("{consumeCreateConfirmation} -> Consuming message with confirmationId -> {}", message);
    String authenticatedUserId = headers.get(Constants.AUTHENTICATED_USER_ID);
    log.info(
        "{consumeCreateConfirmation} -> Consuming message with userId -> {}", authenticatedUserId);
    authenticationHolder.setUserId(authenticatedUserId);
    confirmationService.processConfirmation(message.confirmationId());
  }

  @SqsListener("${sqs.queue.create-confirmation-dlq.name}")
  public void consumeCreateConfirmationFromDlq(@Payload CreateConfirmationMessage message) {
    log.info(
        "{consumeCreateConfirmationFromDlq} -> an error occurred during message receiving, message sent to DLQ");
    long confirmationId = message.confirmationId();
    confirmationService.rejectConfirmation(confirmationId);
    log.info(
        "{consumeCreateConfirmationFromDlq} -> an error occurred and the confirmation {} has been rejected",
        confirmationId);
  }

  @SqsListener("${sqs.queue.send-email.name}")
  public void consumeSendEmail(
      @Payload BaseEmailRequestDto baseEmailRequestDto, @Headers Map<String, String> headers) {
    String authenticatedUserId = headers.get(Constants.AUTHENTICATED_USER_ID);
    log.info(
        "{consumeSendEmail} -> Consuming message with email send request -> {}",
        baseEmailRequestDto);
    authenticationHolder.setUserId(authenticatedUserId);
    emailService.processSendEmail(baseEmailRequestDto);
  }

  @MessageExceptionHandler
  public void handleQueueException(RuntimeException exception) {
    log.error("{handleQueueException} Error occurred: {}", exception.getMessage());
  }
}
