package com.andersenlab.etalon.transactionservice.mapper;

import com.andersenlab.etalon.transactionservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.transactionservice.dto.sqs.TransactionQueueMessage;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionResponseDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  String DATE_PATTERN = "dd.MM.yyyy";

  String TIME_PATTERN = "HH:mm:ss";

  TransactionResponseDto toDto(final TransactionEntity entity);

  @Mapping(target = "details", source = "transactionEntity.details")
  @Mapping(target = "transactionName", source = "transactionEntity.transactionName")
  @Mapping(target = "accountNumberWithdrawn", source = "transactionEntity.senderAccount")
  @Mapping(target = "accountNumberReplenished", source = "transactionEntity.receiverAccount")
  @Mapping(target = "amount", source = "transactionEntity.amount")
  @Mapping(target = "loanInterestAmount", source = "loanInterestAmount")
  @Mapping(target = "currency", source = "transactionEntity.currency")
  @Mapping(target = "feeAmount", source = "transactionEntity.feeAmount")
  @Mapping(target = "loanPenaltyAmount", source = "loanPenaltyAmount")
  @Mapping(
      target = "isFeeProvided",
      source = "transactionEntity",
      qualifiedByName = "isFeeProvided")
  TransactionCreateRequestDto toCreateRequestDto(
      final TransactionEntity transactionEntity,
      final BigDecimal loanInterestAmount,
      final BigDecimal loanPenaltyAmount);

  @Named("isFeeProvided")
  default Boolean isFeeProvided(final TransactionEntity transactionEntity) {
    if (Objects.isNull(transactionEntity.getFeeAmount())) {
      return false;
    }
    return BigDecimal.ZERO.compareTo(transactionEntity.getFeeAmount()) <= 0;
  }

  @Mapping(target = "id", source = "transactionEntity.id")
  @Mapping(target = "transactionDate", source = "transactionEntity", qualifiedByName = "getDate")
  @Mapping(target = "transactionTime", source = "transactionEntity", qualifiedByName = "getTime")
  @Mapping(target = "transactionName", source = "transactionEntity.transactionName")
  @Mapping(target = "outcomeAccountNumber", source = "transactionEntity.senderAccount")
  @Mapping(target = "incomeAccountNumber", source = "transactionEntity.receiverAccount")
  @Mapping(target = "transactionAmount", source = "transactionEntity.amount")
  @Mapping(target = "currency", source = "transactionEntity.currency")
  @Mapping(target = "type", source = "transactionType")
  @Mapping(target = "status", source = "transactionEntity.status")
  TransactionDetailedResponseDto toDetailResponseDto(
      TransactionEntity transactionEntity, BigDecimal amount, Type transactionType);

  @Mapping(target = "transactionId", source = "transactionEntity.id")
  @Mapping(target = "loanInterestAmount", source = "requestDto.loanInterestAmount")
  @Mapping(target = "loanPenaltyAmount", source = "requestDto.loanPenaltyAmount")
  TransactionQueueMessage toQueueMessageDto(
      TransactionEntity transactionEntity, TransactionCreateRequestDto requestDto);

  @Named("getDate")
  default String getDate(TransactionEntity transactionEntity) {
    return DateTimeFormatter.ofPattern(DATE_PATTERN).format(transactionEntity.getProcessedAt());
  }

  @Named("getTime")
  default String getTime(TransactionEntity transactionEntity) {
    return DateTimeFormatter.ofPattern(TIME_PATTERN).format(transactionEntity.getProcessedAt());
  }

  @Mapping(target = "id", source = "transactionEntity.id")
  @Mapping(target = "name", source = "transactionEntity.transactionName")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "totalAmount", source = "transactionEntity.amount")
  @Mapping(target = "accountNumber", expression = "java(mapAccountByType(transactionEntity, type))")
  @Mapping(target = "createdAt", source = "transactionEntity.createAt")
  @Mapping(
      target = "events",
      expression = "java(attachEvents(transactionEntity, withEvents, userAccountNumbers))")
  @Mapping(source = "shortCardInfo", target = "shortCardInfo")
  TransactionExtendedResponseDto toExtendedDto(
      TransactionEntity transactionEntity,
      Boolean withEvents,
      Type type,
      List<String> userAccountNumbers,
      ShortCardInfoDto shortCardInfo);

  @Named("mapAccountByType")
  default String mapAccountByType(TransactionEntity transactionEntity, Type type) {
    if (type == Type.OUTCOME) {
      return transactionEntity.getSenderAccount();
    } else {
      return transactionEntity.getReceiverAccount();
    }
  }

  @Named("attachEvents")
  @SuppressWarnings("java:S1168")
  default List<EventDto> attachEvents(
      TransactionEntity transactionEntity, Boolean withEvents, List<String> userAccountNumbers) {
    if (Boolean.FALSE.equals(withEvents)) {
      return null;
    }
    List<EventEntity> eventEntityList = transactionEntity.getEventEntityList();

    return eventEntityList.stream()
        .filter(
            eventEntity ->
                eventEntity.getAmount().compareTo(BigDecimal.valueOf(0)) != 0
                    && userAccountNumbers.contains(eventEntity.getAccountNumber()))
        .map(
            ee ->
                EventDto.builder()
                    .type(ee.getType())
                    .accountNumber(ee.getAccountNumber())
                    .eventType(ee.getEventType())
                    .currency(ee.getCurrency())
                    .amount(ee.getAmount())
                    .build())
        .toList();
  }

  default List<TransactionExtendedResponseDto> toExtendedDtoList(
      List<TransactionEntity> entityList,
      Boolean withEvents,
      Type type,
      List<String> userAccountNumbers,
      List<ShortCardInfoDto> shortCardInfoDtos) {
    Map<Long, List<ShortCardInfoDto>> shortCardInfoMap =
        shortCardInfoDtos.stream().collect(Collectors.groupingBy(ShortCardInfoDto::id));
    return entityList.stream()
        .map(
            te -> {
              Long cardId =
                  (type.equals(Type.INCOME)) ? te.getReceiverCardId() : te.getSenderCardId();
              ShortCardInfoDto lastCardInfo =
                  Optional.ofNullable(shortCardInfoMap.get(cardId))
                      .filter(list -> !list.isEmpty())
                      .map(list -> list.get(list.size() - 1))
                      .orElse(null);

              return toExtendedDto(te, withEvents, type, userAccountNumbers, lastCardInfo);
            })
        .toList();
  }
}
