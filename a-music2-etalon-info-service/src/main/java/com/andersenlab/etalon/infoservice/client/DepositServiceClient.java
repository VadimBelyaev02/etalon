package com.andersenlab.etalon.infoservice.client;

import com.andersenlab.etalon.infoservice.config.FeignConfig;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "deposit-service",
    url = "${feign.deposit-service.url}",
    configuration = FeignConfig.class,
    path = "/deposit")
public interface DepositServiceClient {
  String API_V1_URI = "/api/v1";
  String DEPOSIT_ORDER_URI = "/deposit-orders";
  String DEPOSIT_ORDER_ID_URI = "/{depositOrderId}";
  String DEPOSIT_ORDER_CONFIRM = "/confirm";
  String DEPOSIT_ORDER_URL = API_V1_URI + DEPOSIT_ORDER_URI;
  String DEPOSIT_ORDER_ID_CONFIRM_URL = DEPOSIT_ORDER_ID_URI + DEPOSIT_ORDER_CONFIRM;

  @PostMapping(DEPOSIT_ORDER_URL + DEPOSIT_ORDER_ID_CONFIRM_URL)
  MessageResponseDto processOpeningNewDeposit(@PathVariable final Long depositOrderId);
}
