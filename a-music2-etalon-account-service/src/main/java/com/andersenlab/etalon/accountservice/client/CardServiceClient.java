package com.andersenlab.etalon.accountservice.client;

import com.andersenlab.etalon.accountservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.accountservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.exceptionhandler.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "card-service",
    url = "${feign.card-service.url}",
    configuration = {AuthenticationContextFeignConfig.class, FeignConfig.class},
    path = "/card")
public interface CardServiceClient {
  String API_V1_URI = "/api/v1";
  String CARD_BY_NUMBER_URI = "/cards-by-number";
  String CARD_NUMBER_PATH = "/{cardNumber}";
  String USER_CARD_BY_NUMBER_URL = API_V1_URI + CARD_BY_NUMBER_URI + CARD_NUMBER_PATH;

  @GetMapping(USER_CARD_BY_NUMBER_URL)
  CardDetailedResponseDto getUserCardByNumber(@PathVariable String cardNumber);
}
