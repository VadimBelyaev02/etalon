package com.andersenlab.etalon.cardservice.controller.impl;

import com.andersenlab.etalon.cardservice.controller.CardController;
import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.RequestFilterDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.cardservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.cardservice.service.CardCreationService;
import com.andersenlab.etalon.cardservice.service.CardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CardController.USER_CARDS_URL)
@RequiredArgsConstructor
public class CardControllerImpl implements CardController {

  private final CardService cardService;
  private final CardCreationService cardCreationService;
  private final AuthenticationHolder authenticationHolder;

  @GetMapping(CardController.ACTIVE_CARD)
  @Override
  public CardResponseDto getActiveUserCard(@RequestParam String accountNumber) {
    return cardService.getActiveUserCardByAccountNumber(accountNumber);
  }

  @GetMapping(CARDS_INFO_URI)
  @Override
  public List<ShortCardInfoDto> getShortCardInfoByCardIds(@RequestParam List<Long> cardId) {
    return cardService.findCardsInfoByCardIds(cardId);
  }

  @PatchMapping(ID_PATH)
  public MessageResponseDto updateCardDetails(
      @PathVariable Long id, @RequestBody final CardDetailsRequestDto cardDetailsRequestDto) {
    cardService.updateCardDetails(id, cardDetailsRequestDto, authenticationHolder.getUserId());

    return new MessageResponseDto("Card details changed successfully");
  }

  @GetMapping
  public List<CardResponseDto> getAllUserCards(RequestFilterDto filters) {
    return cardService.findAllCardsByUserIdAndFilter(authenticationHolder.getUserId(), filters);
  }

  @GetMapping(CARD_ID_PATH)
  public CardDetailedResponseDto getUserCardById(@PathVariable long cardId) {
    return cardService.findCardById(cardId, authenticationHolder.getUserId());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CardDetailedResponseDto openUserCard(@RequestBody CardCreationRequestDto cardCreation) {
    return cardCreationService.createUserCard(cardCreation, authenticationHolder.getUserId());
  }
}
