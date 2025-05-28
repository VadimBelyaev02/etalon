package com.andersenlab.etalon.depositservice.client;

import com.andersenlab.etalon.depositservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.depositservice.dto.auth.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.depositservice.dto.auth.response.CreateConfirmationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
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

  @PostMapping(CONFIRMATIONS_URL)
  CreateConfirmationResponseDto createConfirmation(
      @RequestBody CreateConfirmationRequestDto authConfirmation);
}
