package com.andersenlab.etalon.transactionservice.service.impl;

import static com.andersenlab.etalon.transactionservice.repository.specifications.TransactionSpecifications.*;
import static com.andersenlab.etalon.transactionservice.util.Constants.MONTH_GAP;
import static java.util.function.Predicate.not;

import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.config.TimeProvider;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.sqs.TransactionQueueMessage;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionMessageResponseDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.transactionservice.mapper.EventsMapper;
import com.andersenlab.etalon.transactionservice.mapper.TransactionMapper;
import com.andersenlab.etalon.transactionservice.repository.EventRepository;
import com.andersenlab.etalon.transactionservice.repository.TransactionRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.EventService;
import com.andersenlab.etalon.transactionservice.service.TransactionService;
import com.andersenlab.etalon.transactionservice.service.ValidationService;
import com.andersenlab.etalon.transactionservice.service.strategies.TransactionPostprocessorService;
import com.andersenlab.etalon.transactionservice.sqs.SqsMessageProducer;
import com.andersenlab.etalon.transactionservice.util.Constants;
import com.andersenlab.etalon.transactionservice.util.EnumUtils;
import com.andersenlab.etalon.transactionservice.util.TransactionUtils;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final EventRepository eventRepository;
  private final EventsMapper eventsMapper;
  private final TransactionRepository transactionRepository;
  private final EventService eventService;
  private final ValidationService validationService;
  private final AccountService accountService;
  private final TimeProvider timeProvider;
  private final TransactionPostprocessorService transactionPostprocessorService;
  private final TransactionMapper transactionMapper;
  private final SqsMessageProducer sqsMessageProducer;
  private final AuthenticationHolder authenticationHolder;
  private final CardServiceClient cardServiceClient;

  @Value("${sqs.queue.create-transaction.name}")
  private String transactionQueue;

  @Override
  public TransactionMessageResponseDto createTransaction(
      TransactionCreateRequestDto transactionCreateRequestDto) {

    validationService.validateAmountMoreThanZero(transactionCreateRequestDto.amount());

    CardResponseDto senderCard =
        cardServiceClient.getActiveUserCardByAccountNumber(
            transactionCreateRequestDto.accountNumberWithdrawn());
    CardResponseDto receiverCard =
        cardServiceClient.getActiveUserCardByAccountNumber(
            transactionCreateRequestDto.accountNumberReplenished());

    TransactionEntity transaction =
        new TransactionEntity()
            .toBuilder()
                .status(TransactionStatus.CREATED)
                .senderAccount(transactionCreateRequestDto.accountNumberWithdrawn())
                .receiverAccount(transactionCreateRequestDto.accountNumberReplenished())
                .amount(transactionCreateRequestDto.amount())
                .currency(transactionCreateRequestDto.currency())
                .feeAmount(transactionCreateRequestDto.feeAmount())
                .standardRate(transactionCreateRequestDto.standardRate())
                .details(transactionCreateRequestDto.details())
                .transactionName(transactionCreateRequestDto.transactionName())
                .createAt(timeProvider.getCurrentZonedDateTime())
                .senderCardId(Optional.ofNullable(senderCard).map(CardResponseDto::id).orElse(null))
                .receiverCardId(
                    Optional.ofNullable(receiverCard).map(CardResponseDto::id).orElse(null))
                .build();

    TransactionEntity savedTransaction = transactionRepository.save(transaction);

    TransactionQueueMessage queueMessageDto =
        transactionMapper.toQueueMessageDto(savedTransaction, transactionCreateRequestDto);

    sqsMessageProducer.send(
        transactionQueue,
        queueMessageDto,
        Map.of(
            Constants.AUTHENTICATED_USER_ID,
            Optional.ofNullable(authenticationHolder.getUserId())
                .orElseThrow(
                    () ->
                        new BusinessException(
                            HttpStatus.UNAUTHORIZED,
                            BusinessException.NO_ANY_USER_AUTHORIZED_FOUND))));

    return TransactionMessageResponseDto.builder()
        .messageResponseDto(
            new MessageResponseDto(
                String.format(MessageResponseDto.TRANSACTION_CREATED, savedTransaction.getId())))
        .transactionId(savedTransaction.getId())
        .status(savedTransaction.getStatus().name())
        .build();
  }

  @Override
  public void processTransaction(
      long transactionId, BigDecimal loanInterestAmount, BigDecimal loanPenaltyAmount) {
    TransactionEntity savedTransaction = getTransactionById(transactionId);
    if (!TransactionStatus.CREATED.equals(savedTransaction.getStatus())) {
      log.warn(
          "{processTransaction} -> Transaction with id-{} expected to have status-CREATED, but actual status-{}",
          transactionId,
          savedTransaction.getStatus());
      return;
    }
    changeTransactionStatusAndProcess(TransactionStatus.PROCESSING, savedTransaction);
    TransactionCreateRequestDto transactionCreateRequestDto =
        transactionMapper.toCreateRequestDto(
            savedTransaction, loanInterestAmount, loanPenaltyAmount);
    List<EventEntity> eventsList =
        eventService.createEvents(savedTransaction, transactionCreateRequestDto);
    TransactionEntity processedTransaction =
        Boolean.TRUE.equals(eventService.checkEventsStatus(eventsList))
            ? changeTransactionStatusAndProcess(TransactionStatus.DECLINED, savedTransaction)
            : changeTransactionStatusAndProcess(TransactionStatus.APPROVED, savedTransaction);
    transactionPostprocessorService.postProcess(processedTransaction);
  }

  @Override
  public TransactionEntity getTransactionById(long id) {
    return transactionRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    HttpStatus.NOT_FOUND,
                    BusinessException.TRANSACTION_NOT_FOUND_BY_TRANSACTION_ID.formatted(id)));
  }

  @Override
  public TransactionEntity changeTransactionStatusAndProcess(
      TransactionStatus status, TransactionEntity transaction) {
    transaction.setStatus(status);
    transaction.setProcessedAt(timeProvider.getCurrentZonedDateTime());
    return transactionRepository.save(transaction);
  }

  @Override
  public List<EventResponseDto> getAllUserTransactions(String userId, TransactionFilter filter) {

    Map<String, ZonedDateTime> dates = TransactionUtils.checkFilterDates(filter);

    verifyUserAccountOwnership(userId, filter.getAccountNumber());

    List<String> checkedAccountNumbers =
        TransactionUtils.checkFilterAccountNumber(
            filter, accountService.getUserAccountNumbers(userId));

    if (checkedAccountNumbers.isEmpty()) {
      return Collections.emptyList();
    }

    Predicate<EventEntity> openDepositTransactionPredicate =
        eventEntity -> {
          Details details = eventEntity.getTransactionEntity().getDetails();
          Type type = eventEntity.getType();
          return details == Details.OPEN_DEPOSIT && type == Type.INCOME;
        };

    Predicate<EventEntity> withdrawDepositTransactionPredicate =
        eventEntity -> {
          Details details = eventEntity.getTransactionEntity().getDetails();
          Type type = eventEntity.getType();
          return details == Details.WITHDRAW_DEPOSIT && type == Type.OUTCOME;
        };

    List<EventEntity> eventList =
        eventRepository
            .findAllByAccountNumbersWithFilters(
                checkedAccountNumbers,
                TransactionUtils.getEventTypesFromFilter(filter),
                dates.get(TransactionUtils.START_DATE),
                dates.get(TransactionUtils.END_DATE),
                EnumUtils.getEnumValue(filter.getTransactionGroup(), Details.class),
                EnumUtils.getEnumValue(filter.getTransactionStatus(), TransactionStatus.class),
                filter.getAmountFrom(),
                filter.getAmountTo(),
                TransactionUtils.getPageRequestFromFilter(filter))
            .stream()
            .filter(
                not(openDepositTransactionPredicate).and(not(withdrawDepositTransactionPredicate)))
            .toList();

    return eventsMapper.toListDto(eventList);
  }

  private void verifyUserAccountOwnership(String userId, List<String> accountNumbers) {
    if (Objects.nonNull(accountNumbers) && !accountNumbers.isEmpty()) {
      validationService.validateOwnership(
          accountService.getUserAccountNumbers(userId),
          accountNumbers,
          new BusinessException(
              HttpStatus.BAD_REQUEST,
              BusinessException.VIEW_TRANSACTIONS_REJECTED_DUE_TO_SECURITY));
    }
  }

  @Override
  public Page<TransactionExtendedResponseDto> getAllUserTransactionsExtended(
      String userId, TransactionFilter filter) {
    Map<String, ZonedDateTime> dates = TransactionUtils.checkFilterDates(filter);
    verifyUserAccountOwnership(userId, filter.getAccountNumber());

    List<String> userAccountNumbers = accountService.getUserAccountNumbers(userId);
    List<String> checkedAccountNumbers =
        TransactionUtils.checkFilterAccountNumber(filter, userAccountNumbers);

    if (checkedAccountNumbers.isEmpty()) {
      return Page.empty();
    }
    PageRequest pageRequest = TransactionUtils.getPageRequestFromFilter(filter);
    List<Type> types = TransactionUtils.getEventTypesFromFilter(filter);
    Boolean withEvents = TransactionUtils.getWithEventsConditionFromFilter(filter);
    Map<Type, Page<TransactionExtendedResponseDto>> transactionsWithTypes =
        getPagesOfTransactionsWithTypes(
            checkedAccountNumbers,
            types,
            dates,
            pageRequest,
            withEvents,
            userAccountNumbers,
            filter.getAmountFrom(),
            filter.getAmountTo(),
            EnumUtils.getEnumValue(filter.getTransactionStatus(), TransactionStatus.class),
            EnumUtils.getEnumValue(filter.getTransactionGroup(), Details.class));

    List<TransactionExtendedResponseDto> sortedTransactions =
        getMergedSortedTransactions(transactionsWithTypes, pageRequest);
    Long totalElements = getTotalElementsFromTransactionMap(transactionsWithTypes);

    return new PageImpl<>(sortedTransactions, pageRequest, totalElements);
  }

  private Long getTotalElementsFromTransactionMap(
      Map<Type, Page<TransactionExtendedResponseDto>> transactionsWithTypes) {
    return transactionsWithTypes.values().stream()
        .reduce(0L, (i, page) -> i + page.getTotalElements(), Long::sum);
  }

  private List<TransactionExtendedResponseDto> getMergedSortedTransactions(
      Map<Type, Page<TransactionExtendedResponseDto>> transactionsWithTypes,
      PageRequest pageRequest) {
    return transactionsWithTypes.values().stream()
        .map(Slice::getContent)
        .flatMap(Collection::stream)
        .sorted(TransactionUtils.getSortComparator(pageRequest.getSort()))
        .toList();
  }

  private Map<Type, Page<TransactionExtendedResponseDto>> getPagesOfTransactionsWithTypes(
      List<String> checkedAccountNumbers,
      List<Type> types,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      Boolean withEvents,
      List<String> userAccountNumbers,
      BigDecimal amountFrom,
      BigDecimal amountTo,
      TransactionStatus status,
      Details details) {

    Map<Type, Page<TransactionExtendedResponseDto>> typePageMap = new EnumMap<>(Type.class);
    types.forEach(
        type ->
            typePageMap.put(
                type,
                getPageOfTransactions(
                    checkedAccountNumbers,
                    type,
                    dates,
                    pageRequest,
                    withEvents,
                    userAccountNumbers,
                    amountFrom,
                    amountTo,
                    status,
                    details)));
    return typePageMap;
  }

  private Page<TransactionExtendedResponseDto> getPageOfTransactions(
      List<String> filteredAccounts,
      Type type,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      boolean withEvents,
      List<String> userAccountNumbers,
      BigDecimal amountFrom,
      BigDecimal amountTo,
      TransactionStatus status,
      Details details) {

    if (type == Type.INCOME) {
      return withEvents
          ? getIncomeTransactionsDtoWithEvents(
              filteredAccounts, dates, pageRequest, userAccountNumbers)
          : getIncomeTransactionsDto(
              filteredAccounts,
              dates,
              pageRequest,
              userAccountNumbers,
              amountFrom,
              amountTo,
              status,
              details);
    } else if (type == Type.OUTCOME) {
      return withEvents
          ? getOutcomeTransactionsDtoWithEvents(
              filteredAccounts, dates, pageRequest, userAccountNumbers)
          : getOutcomeTransactionsDto(
              filteredAccounts,
              dates,
              pageRequest,
              userAccountNumbers,
              amountFrom,
              amountTo,
              status,
              details);
    }
    return Page.empty();
  }

  private PageImpl<TransactionExtendedResponseDto> getOutcomeTransactionsDto(
      List<String> filteredAccounts,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      List<String> userAccountNumbers,
      BigDecimal amountFrom,
      BigDecimal amountTo,
      TransactionStatus status,
      Details details) {
    Page<TransactionEntity> page =
        getOutcomeTransactions(
            filteredAccounts, dates, pageRequest, amountFrom, amountTo, status, details);
    List<TransactionExtendedResponseDto> dtoList =
        transactionMapper.toExtendedDtoList(
            page.getContent(), false, Type.OUTCOME, userAccountNumbers, getShortCardInfo(page));
    return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
  }

  private PageImpl<TransactionExtendedResponseDto> getOutcomeTransactionsDtoWithEvents(
      List<String> filteredAccounts,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      List<String> userAccountNumbers) {
    Page<Long> page = getOutcomeTransactionsIds(filteredAccounts, dates, pageRequest);
    List<TransactionEntity> transactions =
        transactionRepository.getTransactionsWithEventsByIds(page.getContent());
    List<TransactionExtendedResponseDto> dtoList =
        transactionMapper.toExtendedDtoList(
            transactions, true, Type.OUTCOME, userAccountNumbers, getShortCardInfo(transactions));
    return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
  }

  private PageImpl<TransactionExtendedResponseDto> getIncomeTransactionsDto(
      List<String> filteredAccounts,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      List<String> userAccountNumbers,
      BigDecimal amountFrom,
      BigDecimal amountTo,
      TransactionStatus status,
      Details details) {
    Page<TransactionEntity> page =
        getIncomeTransactions(
            filteredAccounts, dates, pageRequest, amountFrom, amountTo, status, details);
    List<TransactionExtendedResponseDto> dtoList =
        transactionMapper.toExtendedDtoList(
            page.getContent(), false, Type.INCOME, userAccountNumbers, getShortCardInfo(page));
    return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
  }

  private PageImpl<TransactionExtendedResponseDto> getIncomeTransactionsDtoWithEvents(
      List<String> filteredAccounts,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      List<String> userAccountNumbers) {
    Page<Long> list = getIncomeTransactionsIds(filteredAccounts, dates, pageRequest);
    List<TransactionEntity> transactions =
        transactionRepository.getTransactionsWithEventsByIds(list.getContent());
    List<TransactionExtendedResponseDto> dtoList =
        transactionMapper.toExtendedDtoList(
            transactions, true, Type.INCOME, userAccountNumbers, getShortCardInfo(transactions));
    return new PageImpl<>(dtoList, list.getPageable(), list.getTotalElements());
  }

  private List<ShortCardInfoDto> getShortCardInfo(Iterable<TransactionEntity> transactions) {
    List<Long> cardIds =
        StreamSupport.stream(transactions.spliterator(), false)
            .flatMap(
                transaction ->
                    Stream.of(transaction.getReceiverCardId(), transaction.getSenderCardId()))
            .toList();
    return cardServiceClient.getShortCardInfoByCardIds(cardIds);
  }

  private Page<TransactionEntity> getOutcomeTransactions(
      List<String> filteredAccounts,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      BigDecimal amountFrom,
      BigDecimal amountTo,
      TransactionStatus status,
      Details details) {
    return transactionRepository.findAll(
        Specification.where(
                hasSenderAccounts(filteredAccounts)
                    .and(
                        hasProcessedBetween(
                            dates.get(TransactionUtils.START_DATE),
                            dates.get(TransactionUtils.END_DATE))))
            .and(hasAmountBetween(amountFrom, amountTo))
            .and(hasStatus(status))
            .and(hasDetails(details)),
        pageRequest);
  }

  private Page<TransactionEntity> getIncomeTransactions(
      List<String> filteredAccounts,
      Map<String, ZonedDateTime> dates,
      PageRequest pageRequest,
      BigDecimal amountFrom,
      BigDecimal amountTo,
      TransactionStatus status,
      Details details) {
    return transactionRepository.findAll(
        Specification.where(
                hasReceiverAccounts(filteredAccounts)
                    .and(
                        hasProcessedBetween(
                            dates.get(TransactionUtils.START_DATE),
                            dates.get(TransactionUtils.END_DATE))))
            .and(hasAmountBetween(amountFrom, amountTo))
            .and(hasStatus(status))
            .and(hasDetails(details)),
        pageRequest);
  }

  private Page<Long> getIncomeTransactionsIds(
      List<String> filteredAccounts, Map<String, ZonedDateTime> dates, PageRequest pageRequest) {
    return transactionRepository.getTransactionIdsByProcessedDatesAndReceiverAccounts(
        dates.get(TransactionUtils.START_DATE),
        dates.get(TransactionUtils.END_DATE),
        filteredAccounts,
        pageRequest);
  }

  private Page<Long> getOutcomeTransactionsIds(
      List<String> filteredAccounts, Map<String, ZonedDateTime> dates, PageRequest pageRequest) {
    return transactionRepository.getTransactionIdsByProcessedDatesAndSenderAccounts(
        dates.get(TransactionUtils.START_DATE),
        dates.get(TransactionUtils.END_DATE),
        filteredAccounts,
        pageRequest);
  }

  @Override
  @Transactional(readOnly = true)
  public List<EventResponseDto> getTimeframeTransactionsForAccounts(List<String> accountNumbers) {
    ZonedDateTime startDate = timeProvider.getCurrentZonedDateTime().minusMonths(MONTH_GAP);
    ZonedDateTime endDate = timeProvider.getCurrentZonedDateTime();
    return eventsMapper.toListDto(
        eventRepository.findAllByCreateAtBetweenAndAccountNumberIn(
            startDate, endDate, accountNumbers));
  }

  @Override
  public TransactionDetailedResponseDto getDetailedTransaction(String userId, Long transactionId) {
    List<EventEntity> eventsList = eventRepository.findAllByTransactionEntityId(transactionId);
    List<String> allUserAccounts = accountService.getUserAccountNumbers(userId);
    validationService.validateOwnership(
        allUserAccounts,
        eventsList.stream().map(EventEntity::getAccountNumber).toList(),
        new BusinessException(
            HttpStatus.BAD_REQUEST,
            BusinessException.DETAILED_TRANSACTION_VIEW_REJECTED_DUE_TO_SECURITY));
    TransactionEntity transactionEntity = getTransactionById(transactionId);
    Type transactionType = getTransactionType(allUserAccounts, transactionEntity);
    BigDecimal totalTransactionAmount = getTotalTransactionAmount(eventsList, transactionType);
    return transactionMapper.toDetailResponseDto(
        transactionEntity, totalTransactionAmount, transactionType);
  }

  private Type getTransactionType(
      List<String> allUserAccounts, TransactionEntity transactionEntity) {
    if (allUserAccounts.contains(transactionEntity.getReceiverAccount())) {
      return Type.INCOME;
    } else if (allUserAccounts.contains(transactionEntity.getSenderAccount())) {
      return Type.OUTCOME;
    } else {
      throw new BusinessException(
          HttpStatus.NOT_FOUND, BusinessException.PROVIDED_ACCOUNT_NUMBER_IS_NOT_VALID);
    }
  }

  private BigDecimal getTotalTransactionAmount(List<EventEntity> eventsList, Type transactionType) {
    if (transactionType.equals(Type.INCOME)) {
      return eventsList.stream()
          .filter(eventEntity -> eventEntity.getType().equals(Type.INCOME))
          .map(EventEntity::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
    } else {
      return eventsList.stream()
          .filter(eventEntity -> eventEntity.getType().equals(Type.OUTCOME))
          .map(EventEntity::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .negate();
    }
  }
}
