package com.andersenlab.etalon.cardservice.service;

import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;

public interface ValidationService {

  boolean isValidCardBeforeChangeCardStatus(CardEntity card, String userId);

  boolean isValidBlockingCardReasonBeforeSetBlockingCardReason(String blockingReason);

  boolean isCardExpired(CardEntity card);

  boolean hasLinkedDeposits(String accountNumber);

  void validateAccountToUnblockState(
      AccountDetailedResponseDto accountDetailedResponseDto, String userId);

  void validateAccountToCurrentUserLinked(
      AccountDetailedResponseDto accountDetailedResponseDto, String userId);
}
