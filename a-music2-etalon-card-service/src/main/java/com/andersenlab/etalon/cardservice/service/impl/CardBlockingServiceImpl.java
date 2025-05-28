package com.andersenlab.etalon.cardservice.service.impl;

import static com.andersenlab.etalon.cardservice.exception.BusinessException.CARD_ACCOUNT_LINKED_TO_DEPOSIT;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.NOT_FOUND_CARD_BY_ID;

import com.andersenlab.etalon.cardservice.client.AccountServiceClient;
import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.andersenlab.etalon.cardservice.service.CardBlockingService;
import com.andersenlab.etalon.cardservice.service.ValidationService;
import com.andersenlab.etalon.cardservice.util.enums.CardBlockingReason;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardBlockingServiceImpl implements CardBlockingService {

  private final CardRepository cardRepository;
  private final ValidationService validationService;
  private final AccountServiceClient accountServiceClient;

  private static void handleCardStatusWithBlockingReason(CardEntity card, boolean isCardExpired) {
    log.info(
        "{handleCardStatusWithBlockingReason} -> start to handle unblock with reason is {}",
        card.getBlockingReason());

    switch (card.getBlockingReason()) {
      case DAMAGED -> {
        card.setIsBlocked(false);
        card.setBlockingReason(null);
        card.setStatus(isCardExpired ? CardStatus.EXPIRED : CardStatus.ACTIVE);
      }
      case LOST -> throw new BusinessException(
          HttpStatus.FORBIDDEN, BusinessException.CARD_CANNOT_UNBLOCK);
      case STOLEN_FRAUD -> throw new BusinessException(
          HttpStatus.FORBIDDEN, BusinessException.CARD_CANNOT_UNBLOCK_NEVER);
    }
  }

  @Override
  public List<CardBlockingReasonResponseDto> getReasonsCardBlocking() {
    List<CardBlockingReasonResponseDto> reasons = new ArrayList<>();

    Arrays.stream(CardBlockingReason.values())
        .toList()
        .forEach(
            reasonInfo ->
                reasons.add(
                    CardBlockingReasonResponseDto.builder()
                        .id(reasonInfo.getId())
                        .reason(reasonInfo.name())
                        .description(reasonInfo.getReason())
                        .build()));

    return reasons;
  }

  @Override
  public CardEntity changeUserCardBlockingStatus(ChangeCardStatusRequestDto dto, String userId) {

    CardEntity card = getCardByIdAndUserId(dto.id(), userId);

    if (validationService.hasLinkedDeposits(card.getAccountNumber())) {
      throw new BusinessException(HttpStatus.CONFLICT, CARD_ACCOUNT_LINKED_TO_DEPOSIT);
    }

    if (Boolean.FALSE.equals(card.getIsBlocked())) {
      blockUserCard(card, dto, userId);
      return card;
    } else {
      throw new BusinessException(HttpStatus.CONFLICT, BusinessException.CARD_IS_ALREADY_BLOCKED);
    }
  }

  private CardEntity getCardByIdAndUserId(Long cardId, String userId) {
    return cardRepository
        .findByIdAndUserId(cardId, userId)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND, String.format(NOT_FOUND_CARD_BY_ID, cardId)));
  }

  private MessageResponseDto blockUserCard(
      CardEntity card, ChangeCardStatusRequestDto dto, String userId) {
    log.info("{blockUserCard} -> Start to block card with id {}", dto.id());

    if (validationService.isValidCardBeforeChangeCardStatus(card, userId)
        && Boolean.FALSE.equals(card.getIsBlocked())
        && validationService.isValidBlockingCardReasonBeforeSetBlockingCardReason(
            dto.blockingReason())) {

      card.setIsBlocked(true);
      card.setBlockingReason(CardBlockingReason.valueOf(dto.blockingReason()));
      card.setStatus(CardStatus.BLOCKED);
      cardRepository.save(card);

      log.info("{blockUserCard} -> Card with id {} was blocked successfully.", card.getId());
      return new MessageResponseDto(MessageResponseDto.CARD_BLOCK);
    } else {
      log.info(
          "{blockUserCard} -> {} ",
          BusinessException.NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST);

      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST);
    }
  }

  private MessageResponseDto unblockUserCard(CardEntity card, String userId) {
    log.info("{unblockUserCard} -> Start to unblock card with id {}", card.getId());

    if (validationService.isValidCardBeforeChangeCardStatus(card, userId)
        && Boolean.TRUE.equals(card.getIsBlocked())) {
      boolean isCardExpired = validationService.isCardExpired(card);
      handleCardStatusWithBlockingReason(card, isCardExpired);

      changeAccountBlockingStatus(card.getAccountNumber(), "Unblocking", userId);

      cardRepository.save(card);

      log.info("{unblockUserCard} -> Card with id {} was unblocked successfully.", card.getId());
      return new MessageResponseDto(MessageResponseDto.CARD_UNBLOCK);
    } else {
      log.info(
          "{unblockUserCard} -> {} ",
          BusinessException.NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST);

      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST);
    }
  }

  public void changeAccountBlockingStatus(String accountNumber, String operation, String userId) {
    if (Boolean.TRUE.equals(isSingleCardForAccount(userId, accountNumber))) {
      Boolean isSuccess = accountServiceClient.changeIsBlocked(accountNumber);
      if (Boolean.FALSE.equals(isSuccess)) {
        throw new BusinessException(
            HttpStatus.BAD_REQUEST, String.format("%s has been failed", operation));
      }
    }
  }

  private Boolean isSingleCardForAccount(String userId, String accountNumber) {
    return cardRepository.findAllByUserId(userId).stream()
            .filter(a -> a.getAccountNumber().equals(accountNumber))
            .toList()
            .size()
        == 1;
  }
}
