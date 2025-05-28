package com.andersenlab.etalon.cardservice.service;

import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;

public interface CardCreationService {
  CardDetailedResponseDto createUserCard(CardCreationRequestDto cardCreation, String userId);

  CardDetailedResponseDto reissueCard(CardEntity card, String userId);
}
