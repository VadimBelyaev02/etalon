package com.andersenlab.etalon.transactionservice.service.impl;

import com.andersenlab.etalon.transactionservice.client.AccountServiceClient;
import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.config.TimeProvider;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.repository.EventRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.EventService;
import com.andersenlab.etalon.transactionservice.util.DecimalUtils;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.EventStatus;
import com.andersenlab.etalon.transactionservice.util.enums.EventType;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

  public static final String BANK_ACCOUNT_NUMBER = "PL05234567840000000000000000";
  public static final CurrencyName BANK_ACCOUNT_CURRENCY = CurrencyName.PLN;
  private final EventRepository eventRepository;
  private final AccountService accountService;
  private final TimeProvider timeProvider;
  private final AccountServiceClient accountServiceClient;
  private final CardServiceClient cardServiceClient;

  @Override
  public List<EventEntity> createEvents(
      TransactionEntity transactionEntity,
      TransactionCreateRequestDto transactionCreateRequestDto) {

    BigDecimal income =
        Objects.nonNull(transactionEntity.getStandardRate())
            ? DecimalUtils.round(
                DecimalUtils.multiply(
                    transactionCreateRequestDto.amount(), transactionEntity.getStandardRate()))
            : transactionCreateRequestDto.amount();

    CurrencyName incomeCurrency =
        accountService
            .getDetailedAccountInfo(transactionCreateRequestDto.accountNumberWithdrawn())
            .currency();

    CurrencyName outcomeCurrency =
        accountService
            .getDetailedAccountInfo(transactionCreateRequestDto.accountNumberReplenished())
            .currency();

    CardResponseDto outcomeCard =
        cardServiceClient.getActiveUserCardByAccountNumber(
            transactionCreateRequestDto.accountNumberWithdrawn());

    CardResponseDto incomeCard =
        cardServiceClient.getActiveUserCardByAccountNumber(
            transactionCreateRequestDto.accountNumberReplenished());

    EventEntity outcomeEvent =
        createNewEvent(
            transactionCreateRequestDto.accountNumberWithdrawn(),
            transactionCreateRequestDto.amount(),
            incomeCurrency,
            transactionEntity,
            Type.OUTCOME,
            EventType.BODY,
            outcomeCard.id());

    EventEntity incomeEvent =
        createNewEvent(
            transactionCreateRequestDto.accountNumberReplenished(),
            income,
            outcomeCurrency,
            transactionEntity,
            Type.INCOME,
            EventType.BODY,
            incomeCard.id());

    List<EventEntity> eventsList = new ArrayList<>();
    eventsList.add(outcomeEvent);
    eventsList.add(incomeEvent);

    if (Objects.nonNull(transactionCreateRequestDto.loanInterestAmount())
        && transactionCreateRequestDto.loanInterestAmount().compareTo(BigDecimal.ZERO) > 0) {
      eventsList.addAll(
          createAdditionalEvents(
              transactionEntity,
              Type.OUTCOME,
              transactionCreateRequestDto.loanInterestAmount(),
              transactionCreateRequestDto.accountNumberWithdrawn(),
              EventType.PERCENTAGE));
    }

    if (Objects.nonNull(transactionCreateRequestDto.isFeeProvided())
        && Boolean.TRUE.equals(transactionCreateRequestDto.isFeeProvided())) {
      eventsList.addAll(
          createAdditionalEvents(
              transactionEntity,
              Type.OUTCOME,
              transactionCreateRequestDto.feeAmount(),
              transactionCreateRequestDto.accountNumberWithdrawn(),
              EventType.FEE));
    }

    if (Objects.nonNull(transactionCreateRequestDto.loanPenaltyAmount())
        && transactionCreateRequestDto.loanPenaltyAmount().compareTo(BigDecimal.ZERO) > 0) {
      eventsList.addAll(
          createAdditionalEvents(
              transactionEntity,
              Type.OUTCOME,
              transactionCreateRequestDto.loanPenaltyAmount(),
              transactionCreateRequestDto.accountNumberWithdrawn(),
              EventType.PENALTY));
    }

    eventsList.stream()
        .filter(event -> event.getStatus().equals(EventStatus.PROCESSING))
        .forEach(event -> event.setStatus(EventStatus.APPROVED));

    eventRepository.saveAll(eventsList);

    return eventsList;
  }

  private List<EventEntity> createAdditionalEvents(
      TransactionEntity transactionEntity,
      Type type,
      BigDecimal eventAmount,
      String accountNumberWithdrawn,
      EventType eventType) {
    log.info("{createAdditionalEvents} -> Creating additional transaction events");
    CurrencyName outcomeCurrency =
        accountService.getDetailedAccountInfo(accountNumberWithdrawn).currency();

    CardResponseDto outcomeCard =
        cardServiceClient.getActiveUserCardByAccountNumber(accountNumberWithdrawn);
    CardResponseDto incomeCard =
        cardServiceClient.getActiveUserCardByAccountNumber(BANK_ACCOUNT_NUMBER);

    EventEntity outcomeEvent =
        createNewEvent(
            accountNumberWithdrawn,
            eventAmount,
            outcomeCurrency,
            transactionEntity,
            type,
            eventType,
            outcomeCard.id());
    EventEntity incomeEvent =
        createNewEvent(
            BANK_ACCOUNT_NUMBER,
            eventAmount,
            BANK_ACCOUNT_CURRENCY,
            transactionEntity,
            Type.INCOME,
            eventType,
            incomeCard.id());
    return List.of(outcomeEvent, incomeEvent);
  }

  private EventEntity createNewEvent(
      String accountNumber,
      BigDecimal amount,
      CurrencyName currency,
      TransactionEntity transactionEntity,
      Type type,
      EventType eventType,
      Long cardId) {
    EventEntity event =
        new EventEntity()
            .toBuilder()
                .accountNumber(accountNumber)
                .amount(amount)
                .currency(currency)
                .type(type)
                .status(EventStatus.PROCESSING)
                .transactionEntity(transactionEntity)
                .createAt(timeProvider.getCurrentZonedDateTime())
                .eventType(eventType)
                .build();
    try {
      if (type.equals(Type.OUTCOME)) {
        accountService.withdrawAccountBalance(
            accountNumber, new AccountWithdrawByAccountNumberRequestDto(amount));
      } else
        accountService.replenishAccountBalance(
            accountNumber, new AccountReplenishByAccountNumberRequestDto(amount));
    } catch (Exception exception) {
      log.error(
          "{createEvents} -> Error in " + type + " event on account with id: {}", accountNumber);
      event.setStatus(EventStatus.DECLINED);
    }
    return event;
  }

  private void declineEvent(EventEntity event) {
    log.info("{declineEvent} -> Declining event with id: {}", event.getId());

    if (event.getType().equals(Type.OUTCOME)) {
      accountService.replenishAccountBalance(
          event.getAccountNumber(),
          new AccountReplenishByAccountNumberRequestDto(event.getAmount()));
    } else
      accountService.withdrawAccountBalance(
          event.getAccountNumber(),
          new AccountWithdrawByAccountNumberRequestDto(event.getAmount()));
  }

  @Override
  public Boolean checkEventsStatus(List<EventEntity> events) {
    boolean areEventsDeclined =
        events.stream().anyMatch(event -> event.getStatus().equals(EventStatus.DECLINED));

    if (areEventsDeclined) {
      events.stream()
          .filter(event -> event.getStatus().equals(EventStatus.APPROVED))
          .forEach(this::declineEvent);
    }
    return areEventsDeclined;
  }
}
