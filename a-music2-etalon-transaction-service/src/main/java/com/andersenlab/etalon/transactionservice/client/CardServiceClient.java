package com.andersenlab.etalon.transactionservice.client;

import com.andersenlab.etalon.transactionservice.config.AuthenticationContextFeignConfig;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.ShortCardInfoDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "card-service",
    url = "${feign.card-service.url}",
    configuration = AuthenticationContextFeignConfig.class,
    path = "/card")
public interface CardServiceClient {
  String API_V1_URI = "/api/v1";
  String CARDS_URI = "/cards";
  String API_V1_CARDS_URI = API_V1_URI + CARDS_URI;
  String CARD_BY_NUMBER_URI = "/cards-by-number";
  String CARD_NUMBER_PATH = "/{cardNumber}";
  String USER_CARD_BY_NUMBER_URL = API_V1_URI + CARD_BY_NUMBER_URI + CARD_NUMBER_PATH;
  String CARDS_INFO_URI = "/cards-info";
  String USER_CARD_BY_ACCOUNT_NUMBER_URL = API_V1_URI + CARDS_URI + CARDS_INFO_URI;
  String ACTIVE_CARD = "/active";
  String ACTIVE_USER_CARD_URL = API_V1_CARDS_URI + ACTIVE_CARD;

  @GetMapping(USER_CARD_BY_NUMBER_URL)
  AccountRequestDto getUserCardByNumber(@PathVariable String cardNumber);

  @GetMapping(API_V1_CARDS_URI)
  List<CardResponseDto> getAllUserCards(@RequestParam String accountNumber);

  @GetMapping(USER_CARD_BY_ACCOUNT_NUMBER_URL)
  List<ShortCardInfoDto> getShortCardInfoByCardIds(@RequestParam List<Long> cardId);

  @GetMapping(ACTIVE_USER_CARD_URL)
  CardResponseDto getActiveUserCardByAccountNumber(@RequestParam String accountNumber);
}
