package com.andersenlab.etalon.cardservice.service;

import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import java.util.List;

public interface CardBlockingService {

  List<CardBlockingReasonResponseDto> getReasonsCardBlocking();

  CardEntity changeUserCardBlockingStatus(ChangeCardStatusRequestDto dto, String userId);

  void changeAccountBlockingStatus(String accountNumber, String operation, String userId);
}
