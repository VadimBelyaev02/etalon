package com.andersenlab.etalon.cardservice.controller.impl;

import com.andersenlab.etalon.cardservice.controller.CardByNumberController;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.cardservice.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardByNumberControllerImpl implements CardByNumberController {
  private final CardService cardService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(USER_CARD_BY_NUMBER_URL)
  public CardDetailedResponseDto getUserCardByNumber(@PathVariable String cardNumber) {
    return cardService.findCardByNumber(cardNumber);
  }
}
