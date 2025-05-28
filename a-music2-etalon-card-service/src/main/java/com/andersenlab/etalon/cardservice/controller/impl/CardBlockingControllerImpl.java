package com.andersenlab.etalon.cardservice.controller.impl;

import com.andersenlab.etalon.cardservice.controller.CardBlockingController;
import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.cardservice.service.CardBlockingService;
import com.andersenlab.etalon.cardservice.service.CardCreationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CardBlockingController.BLOCK_CARD_URL)
@RequiredArgsConstructor
public class CardBlockingControllerImpl implements CardBlockingController {

  private final CardBlockingService cardBlockingService;
  private final AuthenticationHolder authenticationHolder;
  private final CardCreationService cardCreationService;

  @GetMapping(REASON_BLOCKING_URI)
  public List<CardBlockingReasonResponseDto> getReasonsCardBlocking() {
    return cardBlockingService.getReasonsCardBlocking();
  }

  @PutMapping
  public CardDetailedResponseDto blockCardAndReissue(
      @RequestBody final ChangeCardStatusRequestDto dto) {
    CardEntity cardBlocked =
        cardBlockingService.changeUserCardBlockingStatus(dto, authenticationHolder.getUserId());
    return cardCreationService.reissueCard(cardBlocked, authenticationHolder.getUserId());
  }
}
