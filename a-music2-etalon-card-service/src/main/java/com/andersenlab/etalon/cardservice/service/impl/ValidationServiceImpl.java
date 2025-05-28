package com.andersenlab.etalon.cardservice.service.impl;

import com.andersenlab.etalon.cardservice.client.DepositServiceClient;
import com.andersenlab.etalon.cardservice.config.TimeProvider;
import com.andersenlab.etalon.cardservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.deposit.DepositStatus;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.service.CardService;
import com.andersenlab.etalon.cardservice.service.ValidationService;
import com.andersenlab.etalon.cardservice.util.enums.CardBlockingReason;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.filter.DepositFilterRequest;
import com.andersenlab.etalon.cardservice.util.filter.PaginatedDepositResponse;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

  private final TimeProvider timeProvider;
  private final DepositServiceClient depositServiceClient;
  private final CardService cardService;

  @Override
  public boolean isValidCardBeforeChangeCardStatus(CardEntity card, String userId) {

    Set<CardStatus> validStatuses = Set.of(CardStatus.ACTIVE, CardStatus.BLOCKED);

    return userId.equals(card.getUserId())
        && !isCardExpired(card)
        && validStatuses.contains(card.getStatus());
  }

  @Override
  public boolean isValidBlockingCardReasonBeforeSetBlockingCardReason(String blockingReason) {

    if (Objects.isNull(blockingReason)) return false;

    return Set.of(
            CardBlockingReason.LOST.name(),
            CardBlockingReason.DAMAGED.name(),
            CardBlockingReason.STOLEN_FRAUD.name())
        .contains(blockingReason.toUpperCase());
  }

  @Override
  public boolean isCardExpired(CardEntity card) {
    return timeProvider.getCurrentZonedDateTime().isAfter(card.getExpirationDate());
  }

  @Override
  public boolean hasLinkedDeposits(String accountNumber) {

    List<DepositStatus> linkedDepositStatuses = List.of(DepositStatus.ACTIVE, DepositStatus.CLOSED);

    DepositFilterRequest filter =
        DepositFilterRequest.builder()
            .accountNumber(accountNumber)
            .statusList(linkedDepositStatuses)
            .build();

    PaginatedDepositResponse depositResponse =
        depositServiceClient.getFilteredDepositsByUserId(
            filter.accountNumber(), filter.statusList());

    return depositResponse.getTotalElements() > 0;
  }

  @Override
  public void validateAccountToUnblockState(
      AccountDetailedResponseDto accountDetailedResponseDto, String userId) {
    if (Boolean.TRUE.equals(accountDetailedResponseDto.isBlocked())) {
      throw new BusinessException(HttpStatus.CONFLICT, BusinessException.ACCOUNT_IS_BLOCKED);
    }
  }

  @Override
  public void validateAccountToCurrentUserLinked(
      AccountDetailedResponseDto accountDetailedResponseDto, String userId) {
    if (!userId.equals(accountDetailedResponseDto.userId())) {
      throw new BusinessException(
          HttpStatus.FORBIDDEN, BusinessException.ACCOUNT_LINKED_TO_ANOTHER_USER);
    }
  }
}
