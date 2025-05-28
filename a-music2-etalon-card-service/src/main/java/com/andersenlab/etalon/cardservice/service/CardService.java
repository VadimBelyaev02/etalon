package com.andersenlab.etalon.cardservice.service;

import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.RequestFilterDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import java.util.List;

public interface CardService {
  CardResponseDto getActiveUserCardByAccountNumber(String accountNumber);

  List<CardResponseDto> findAllCardsByUserIdAndFilter(String userId, RequestFilterDto filters);

  CardDetailedResponseDto findCardById(long cardId, String userId);

  void updateCardDetails(Long id, CardDetailsRequestDto cardDetailsRequestDto, String userId);

  CardDetailedResponseDto findCardByNumber(String sourceCardNumber);

  List<ShortCardInfoDto> findCardsInfoByCardIds(List<Long> cardIds);
}
