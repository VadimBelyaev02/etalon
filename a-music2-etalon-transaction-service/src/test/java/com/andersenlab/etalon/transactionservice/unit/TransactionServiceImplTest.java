package com.andersenlab.etalon.transactionservice.unit;

import static com.andersenlab.etalon.transactionservice.MockData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.config.TimeProvider;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
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
import com.andersenlab.etalon.transactionservice.service.impl.EventServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.TransactionServiceImpl;
import com.andersenlab.etalon.transactionservice.service.impl.ValidationServiceImpl;
import com.andersenlab.etalon.transactionservice.service.strategies.TransactionPostprocessorService;
import com.andersenlab.etalon.transactionservice.sqs.SqsMessageProducer;
import com.andersenlab.etalon.transactionservice.util.EnumUtils;
import com.andersenlab.etalon.transactionservice.util.TransactionUtils;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
  public static final String USER_ID = "1L";

  private TransactionEntity transactionEntity;
  private TransactionCreateRequestDto transactionCreateRequestDto;
  private EventEntity eventEntity;
  private TransactionDetailedResponseDto transactionDetailedResponseDto;
  private CardResponseDto cardResponseDto;

  @Mock private TransactionPostprocessorService transactionPostprocessorService;
  @Mock private TransactionRepository transactionRepository;
  @Mock private EventRepository eventRepository;
  @Mock private AccountService accountService;
  @Mock private ValidationServiceImpl validationService;
  @Mock private EventServiceImpl eventService;
  @Mock private TimeProvider timeProvider;
  @Spy private TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);
  @Mock private SqsMessageProducer sqsMessageProducer;
  @Mock private AuthenticationHolder authenticationHolder;
  @Mock private CardServiceClient cardServiceClient;
  @Spy private EventsMapper eventsMapper = Mappers.getMapper(EventsMapper.class);
  @InjectMocks private TransactionServiceImpl underTest;

  @BeforeEach
  void setUp() {
    eventEntity = getValidEventEntity();
    transactionEntity = getValidTransactionEntity();
    transactionCreateRequestDto = getValidTransactionCreateRequestDto();
    transactionDetailedResponseDto = getValidTransactionDetailedResponseDto();
    cardResponseDto = getValidCardResponseDto();
  }

  @Test
  void whenGetAllTransactions_thenSuccess() {
    // given
    List<String> accountsNumbers = getValidAccountNumbersDtoList();
    List<EventEntity> eventEntityList = MockData.getValidEventEntityDtoList();
    List<EventResponseDto> eventResponseDtos = MockData.getValidEventResponseDtoList();
    TransactionFilter filter = MockData.getValidTransactionFilter();

    when(eventRepository.findAllByAccountNumbersWithFilters(
            accountsNumbers,
            List.of(Type.INCOME),
            ZonedDateTime.parse(filter.getStartDate()),
            ZonedDateTime.parse(filter.getEndDate()),
            EnumUtils.getEnumValue(filter.getTransactionGroup(), Details.class),
            EnumUtils.getEnumValue(filter.getTransactionStatus(), TransactionStatus.class),
            filter.getAmountFrom(),
            filter.getAmountTo(),
            PageRequest.of(0, 500, Sort.by("createAt").descending())))
        .thenReturn(eventEntityList);
    when(accountService.getUserAccountNumbers(USER_ID)).thenReturn(accountsNumbers);

    // when
    List<EventResponseDto> result = underTest.getAllUserTransactions(USER_ID, filter);

    // then
    verify(eventRepository, times(1))
        .findAllByAccountNumbersWithFilters(
            accountsNumbers,
            List.of(Type.INCOME),
            ZonedDateTime.parse(filter.getStartDate()),
            ZonedDateTime.parse(filter.getEndDate()),
            Details.PAYMENT,
            TransactionStatus.APPROVED,
            BigDecimal.ZERO,
            new BigDecimal(1000),
            PageRequest.of(0, 500, Sort.by("createAt").descending()));
    verify(accountService, times(1)).getUserAccountNumbers(USER_ID);
    assertEquals(eventResponseDtos.size(), result.size());
    assertEquals(eventResponseDtos.get(0).amount(), result.get(0).amount());
    assertEquals(eventResponseDtos.get(0).id(), result.get(0).id());
    assertEquals(eventResponseDtos.get(0).createAt(), result.get(0).createAt());
    assertEquals(eventResponseDtos.get(0).type(), result.get(0).type());
    assertEquals(eventResponseDtos.get(0).status(), result.get(0).status());
    assertEquals(eventResponseDtos.get(0).name(), result.get(0).name());
    assertEquals(eventResponseDtos.get(0).eventType(), result.get(0).eventType());
  }

  @Test
  void
      givenFilterWithIncomeAndSortByCreatedDateDesc_whenGetAllUserTransactionsExtended_thenSuccess() {
    // given
    List<String> accountsNumbers = getValidAccountNumbersDtoList();
    TransactionFilter filter = MockData.getValidTransactionFilter();
    Page<TransactionEntity> transactionEntityPage =
        MockData.getValidTransactionEntityPage(TransactionUtils.getPageRequestFromFilter(filter));

    when(transactionRepository.getTransactionIdsByProcessedDatesAndReceiverAccounts(
            any(), any(), any(), any()))
        .thenReturn(Page.empty());
    when(transactionRepository.getTransactionsWithEventsByIds(any()))
        .thenReturn(transactionEntityPage.getContent());
    when(accountService.getUserAccountNumbers(USER_ID)).thenReturn(accountsNumbers);

    // when
    Page<TransactionExtendedResponseDto> extended =
        underTest.getAllUserTransactionsExtended(USER_ID, filter);

    // then
    assertNotNull(extended);
    assertThat(extended.getContent()).hasSize(2);
    assertThat(extended.getContent().get(0).createdAt())
        .isEqualTo(MockData.getValidTransactionEntity2().getCreateAt());
    assertThat(extended.getContent().get(1).events()).isNotNull();
    assertThat(extended.getContent().get(1).events().get(0).amount())
        .isEqualTo(MockData.getValidUnboundEventEntity().getAmount());
  }

  @Test
  void
      givenFilterWithIncomeAndSortByCreatedDateAsc_whenGetAllUserTransactionsExtended_thenSuccess() {
    // given
    List<String> accountsNumbers = getValidAccountNumbersDtoList();
    TransactionFilter filter = MockData.getValidTransactionFilter2();
    Page<TransactionEntity> transactionEntityPage =
        MockData.getValidTransactionEntityPage(TransactionUtils.getPageRequestFromFilter(filter));

    when(transactionRepository.getTransactionIdsByProcessedDatesAndReceiverAccounts(
            any(), any(), any(), any()))
        .thenReturn(Page.empty());
    when(transactionRepository.getTransactionsWithEventsByIds(any()))
        .thenReturn(transactionEntityPage.getContent());
    when(accountService.getUserAccountNumbers(USER_ID)).thenReturn(accountsNumbers);

    // when
    Page<TransactionExtendedResponseDto> extended =
        underTest.getAllUserTransactionsExtended(USER_ID, filter);

    // then
    assertNotNull(extended);
    assertThat(extended.getContent()).hasSize(2);
    assertThat(extended.getContent().get(0).createdAt())
        .isEqualTo(MockData.getValidTransactionEntity().getCreateAt());
    assertThat(extended.getContent().get(1).events()).hasSize(1);
  }

  @Test
  void whenCreateTransactionWithEventsApproved_thenSuccess() {
    // given
    when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(transactionRepository.findById(transactionEntity.getId()))
        .thenReturn(Optional.of(transactionEntity));
    when(authenticationHolder.getUserId()).thenReturn("1");
    // when
    when(cardServiceClient.getActiveUserCardByAccountNumber(any())).thenReturn(cardResponseDto);
    TransactionMessageResponseDto intermediateResult =
        underTest.createTransaction(transactionCreateRequestDto);
    underTest.processTransaction(
        intermediateResult.transactionId(), BigDecimal.ZERO, BigDecimal.ZERO);

    // then
    verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
  }

  @Test
  void whenCreateTransactionWithEventsDeclined_thenFail() {
    // given
    MessageResponseDto expectedResult =
        new MessageResponseDto(
            String.format(MessageResponseDto.TRANSACTION_CREATED, transactionEntity.getId()));

    when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(transactionEntity);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(transactionRepository.findById(transactionEntity.getId()))
        .thenReturn(Optional.of(transactionEntity));
    when(authenticationHolder.getUserId()).thenReturn("1");
    when(cardServiceClient.getActiveUserCardByAccountNumber(any())).thenReturn(cardResponseDto);
    // when
    TransactionMessageResponseDto actualResult =
        underTest.createTransaction(transactionCreateRequestDto);
    underTest.processTransaction(actualResult.transactionId(), BigDecimal.ZERO, BigDecimal.ZERO);

    // then
    verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    assertEquals(expectedResult, actualResult.messageResponseDto());
  }

  @Test
  void whenGetDetailedTransactionWithStatusApprovedAndTypeIncome_thenSuccess() {
    // given
    when(eventRepository.findAllByTransactionEntityId(transactionDetailedResponseDto.id()))
        .thenReturn(
            List.of(
                eventEntity,
                eventEntity.toBuilder().type(Type.OUTCOME).accountNumber("2").build()));
    when(accountService.getUserAccountNumbers(USER_ID)).thenReturn(getValidAccountNumbersDtoList());
    when(transactionRepository.findById(anyLong()))
        .thenReturn(Optional.ofNullable(getValidTransactionEntity()));
    when(transactionMapper.toDetailResponseDto(
            any(TransactionEntity.class), any(BigDecimal.class), any(Type.class)))
        .thenReturn(transactionDetailedResponseDto);
    // when
    TransactionDetailedResponseDto actualResult =
        underTest.getDetailedTransaction(USER_ID, transactionDetailedResponseDto.id());

    // then
    verify(eventRepository, times(1))
        .findAllByTransactionEntityId(transactionDetailedResponseDto.id());
    verify(accountService, times(1)).getUserAccountNumbers(anyString());

    assertEquals(transactionDetailedResponseDto.id(), actualResult.id());
    assertEquals(transactionDetailedResponseDto.transactionDate(), actualResult.transactionDate());
    assertEquals(transactionDetailedResponseDto.transactionTime(), actualResult.transactionTime());
    assertEquals(transactionDetailedResponseDto.transactionName(), actualResult.transactionName());
    assertEquals(
        transactionDetailedResponseDto.incomeAccountNumber(), actualResult.incomeAccountNumber());
    assertEquals(transactionDetailedResponseDto.status(), actualResult.status());
    assertEquals(transactionDetailedResponseDto.type(), actualResult.type());
    assertEquals(
        transactionDetailedResponseDto.outcomeAccountNumber(), actualResult.outcomeAccountNumber());
    assertEquals(
        transactionDetailedResponseDto.transactionAmount(), actualResult.transactionAmount());
    assertEquals(transactionDetailedResponseDto.eventType(), actualResult.eventType());
  }

  @Test
  void whenGetDetailedTransactionWithStatusDeclinedAndTypeOutcome_thenSuccess() {
    // given
    transactionDetailedResponseDto =
        getValidTransactionDetailedResponseDto().toBuilder()
            .type(Type.OUTCOME)
            .status(TransactionStatus.DECLINED)
            .build();
    eventEntity = getValidEventEntityWithDeclinedTransactionStatus();
    when(eventRepository.findAllByTransactionEntityId(transactionDetailedResponseDto.id()))
        .thenReturn(
            List.of(
                eventEntity,
                eventEntity.toBuilder().type(Type.OUTCOME).accountNumber("2").build()));
    when(accountService.getUserAccountNumbers(USER_ID)).thenReturn(getValidAccountNumbersDtoList());
    when(transactionRepository.findById(anyLong()))
        .thenReturn(
            Optional.ofNullable(
                getValidTransactionEntity().toBuilder()
                    .status(TransactionStatus.DECLINED)
                    .build()));
    when(transactionMapper.toDetailResponseDto(
            any(TransactionEntity.class), any(BigDecimal.class), any(Type.class)))
        .thenReturn(transactionDetailedResponseDto);
    // when
    TransactionDetailedResponseDto actualResult =
        underTest.getDetailedTransaction(USER_ID, transactionDetailedResponseDto.id());

    // then
    verify(eventRepository, times(1))
        .findAllByTransactionEntityId(transactionDetailedResponseDto.id());
    verify(accountService, times(1)).getUserAccountNumbers(anyString());

    assertEquals(transactionDetailedResponseDto.id(), actualResult.id());
    assertEquals("05.09.2023", actualResult.transactionDate());
    assertEquals("11:20:00", actualResult.transactionTime());
    assertEquals(transactionDetailedResponseDto.transactionName(), actualResult.transactionName());
    assertEquals(
        transactionDetailedResponseDto.incomeAccountNumber(), actualResult.incomeAccountNumber());
    assertEquals(TransactionStatus.DECLINED, actualResult.status());
    assertEquals(Type.OUTCOME, actualResult.type());
    assertEquals(
        transactionDetailedResponseDto.outcomeAccountNumber(), actualResult.outcomeAccountNumber());
    assertEquals(
        transactionDetailedResponseDto.transactionAmount(), actualResult.transactionAmount());
    assertEquals(transactionDetailedResponseDto.eventType(), actualResult.eventType());
  }

  @Test
  void whenGetDetailedTransactionWithoutOutcomeEvent_thenFail() {
    // given
    Long transactionId = transactionDetailedResponseDto.id();
    when(eventRepository.findAllByTransactionEntityId(transactionId))
        .thenReturn(List.of(eventEntity));

    // when/then
    assertThrows(
        BusinessException.class, () -> underTest.getDetailedTransaction(USER_ID, transactionId));
  }

  @Test
  void whenGetDetailedTransactionWithAccountsBelongingToOtherUser_thenFail() {
    // given
    Long transactionId = transactionDetailedResponseDto.id();
    when(eventRepository.findAllByTransactionEntityId(transactionId))
        .thenReturn(
            List.of(
                eventEntity,
                eventEntity.toBuilder().type(Type.OUTCOME).accountNumber("2").build()));
    doThrow(BusinessException.class)
        .when(validationService)
        .validateOwnership(anyList(), anyList(), any(BusinessException.class));

    // when/then
    assertThrows(
        BusinessException.class, () -> underTest.getDetailedTransaction(USER_ID, transactionId));
  }

  @Test
  void whenCreateTransactionWithIncorrectAmount_thenFail() {
    // given
    doThrow(BusinessException.class)
        .when(validationService)
        .validateAmountMoreThanZero(any(BigDecimal.class));

    // when / then
    assertThrows(
        BusinessException.class, () -> underTest.createTransaction(transactionCreateRequestDto));
  }
}
