package com.andersenlab.etalon.userservice.client;

import com.andersenlab.etalon.userservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.userservice.dto.info.request.BaseEmailRequestDto;
import com.andersenlab.etalon.userservice.dto.info.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.info.response.ConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.userservice.util.Operation;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "info-service",
    url = "${feign.info-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/info")
public interface InfoServiceClient {
  String API_V1_URI = "/api/v1";
  String CONFIRMATIONS_URI = "/confirmations";
  String CONFIRMATIONS_URL = API_V1_URI + CONFIRMATIONS_URI;
  String SEND_EMAIL_URI = "/send-email";
  String CONFIRMATION_ID_URI = "/{confirmationId}";
  String CONFIRMATION_RESEND = "/resend";
  String CONFIRMATION_RESEND_BY_ID_URL =
      API_V1_URI + CONFIRMATIONS_URI + CONFIRMATION_ID_URI + CONFIRMATION_RESEND;
  String INTERNAL_URI = "/internal";
  String OPERATION_URI = "/{operation}";
  String TARGET_ID_URI = "/{targetId}";
  String INTERNAL_GET_LAST_CONFIRMATIONS_BY_OPERATION_AND_TARGET_ID_URI =
      INTERNAL_URI + API_V1_URI + CONFIRMATIONS_URI + OPERATION_URI + TARGET_ID_URI;

  @PostMapping(CONFIRMATIONS_URL)
  CreateConfirmationResponseDto createConfirmation(
      @RequestBody CreateConfirmationRequestDto authConfirmation);

  @PostMapping(API_V1_URI + SEND_EMAIL_URI)
  void sendEmail(BaseEmailRequestDto emailRequest);

  @PostMapping(CONFIRMATION_RESEND_BY_ID_URL)
  CreateConfirmationResponseDto resendConfirmationCode(@PathVariable final Long confirmationId);

  @GetMapping(INTERNAL_GET_LAST_CONFIRMATIONS_BY_OPERATION_AND_TARGET_ID_URI)
  List<ConfirmationResponseDto> getConfirmationsByOperationAndTargetId(
      @PathVariable final Operation operation, @PathVariable final Long targetId);
}
