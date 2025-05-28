package com.andersenlab.etalon.transactionservice.unit;

import static com.andersenlab.etalon.transactionservice.MockData.getValidAccountDetailedResponseDto;
import static com.andersenlab.etalon.transactionservice.MockData.getValidCardResponseDto;
import static com.andersenlab.etalon.transactionservice.MockData.getValidEventEntity;
import static com.andersenlab.etalon.transactionservice.MockData.getValidTransactionCreateRequestDto;
import static com.andersenlab.etalon.transactionservice.MockData.getValidTransactionCreateRequestDtoWithFee;
import static com.andersenlab.etalon.transactionservice.MockData.getValidTransactionEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.transactionservice.MockData;
import com.andersenlab.etalon.transactionservice.client.CardServiceClient;
import com.andersenlab.etalon.transactionservice.config.TimeProvider;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.entity.EventEntity;
import com.andersenlab.etalon.transactionservice.entity.TransactionEntity;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.repository.EventRepository;
import com.andersenlab.etalon.transactionservice.service.AccountService;
import com.andersenlab.etalon.transactionservice.service.impl.EventServiceImpl;
import com.andersenlab.etalon.transactionservice.util.enums.EventStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

  private EventEntity eventEntity;
  private TransactionEntity transactionEntity;
  private TransactionCreateRequestDto transactionCreateRequestDto;
  private TransactionCreateRequestDto transactionCreateRequestDtoWithFee;
  private TransactionCreateRequestDto transactionCreateRequestDtoWithLoanInterest;
  private AccountDetailedResponseDto accountDetailedResponseDto;
  private CardResponseDto cardResponseDto;
  private List<EventEntity> eventsList;

  @Mock private EventRepository eventRepository;
  @Mock private AccountService accountService;
  @Mock private CardServiceClient cardServiceClient;
  @Mock private TimeProvider timeProvider;
  @InjectMocks private EventServiceImpl underTest;

  @BeforeEach
  void setUp() {
    eventEntity = getValidEventEntity();
    transactionEntity = getValidTransactionEntity();
    transactionCreateRequestDto = getValidTransactionCreateRequestDto();
    accountDetailedResponseDto = getValidAccountDetailedResponseDto();
    transactionCreateRequestDtoWithFee = getValidTransactionCreateRequestDtoWithFee();
    transactionCreateRequestDtoWithLoanInterest =
        MockData.getValidTransactionCreateRequestDtoWithLoanInterest();
    eventsList =
        List.of(
            getValidEventEntity(),
            eventEntity.toBuilder().type(Type.OUTCOME).build(),
            eventEntity.toBuilder().type(Type.OUTCOME).amount(BigDecimal.ONE).build(),
            new EventEntity().toBuilder().type(Type.INCOME).amount(BigDecimal.ONE).build());
    cardResponseDto = getValidCardResponseDto();
  }

  @Test
  void whenCreateEvents_thenEventsApproved() {
    // given
    when(accountService.replenishAccountBalance(
            anyString(), any(AccountReplenishByAccountNumberRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(accountService.withdrawAccountBalance(
            anyString(), any(AccountWithdrawByAccountNumberRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(eventRepository.saveAll(anyList())).thenReturn(eventsList);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(accountService.getDetailedAccountInfo(anyString())).thenReturn(accountDetailedResponseDto);
    when(cardServiceClient.getActiveUserCardByAccountNumber(anyString()))
        .thenReturn(cardResponseDto);
    // when
    List<EventEntity> actualResult =
        underTest.createEvents(transactionEntity, transactionCreateRequestDto);

    // then
    verify(eventRepository, times(1)).saveAll(anyList());
    verify(accountService, times(1))
        .replenishAccountBalance(anyString(), any(AccountReplenishByAccountNumberRequestDto.class));
    verify(accountService, times(1))
        .withdrawAccountBalance(anyString(), any(AccountWithdrawByAccountNumberRequestDto.class));

    assertEquals(
        transactionCreateRequestDto.accountNumberWithdrawn(),
        actualResult.get(0).getAccountNumber());
    assertEquals(
        transactionCreateRequestDto.accountNumberReplenished(),
        actualResult.get(1).getAccountNumber());
    assertEquals(EventStatus.APPROVED, actualResult.get(0).getStatus());
    assertEquals(EventStatus.APPROVED, actualResult.get(1).getStatus());
    assertEquals(Type.OUTCOME, actualResult.get(0).getType());
    assertEquals(Type.INCOME, actualResult.get(1).getType());
    assertEquals(transactionCreateRequestDto.amount(), actualResult.get(0).getAmount());
    assertEquals(transactionCreateRequestDto.amount(), actualResult.get(1).getAmount());
  }

  @Test
  void whenCreateEvents_thenEventsDeclined() {
    // given
    when(accountService.replenishAccountBalance(
            anyString(), any(AccountReplenishByAccountNumberRequestDto.class)))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.NOT_FOUND_ACCOUNT_BY_ID.formatted(
                    transactionCreateRequestDto.accountNumberReplenished())));
    when(accountService.withdrawAccountBalance(
            anyString(), any(AccountWithdrawByAccountNumberRequestDto.class)))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.NOT_FOUND_ACCOUNT_BY_NUMBER.formatted(
                    transactionCreateRequestDto.accountNumberReplenished())));
    when(eventRepository.saveAll(anyList())).thenReturn(eventsList);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(accountService.getDetailedAccountInfo(anyString())).thenReturn(accountDetailedResponseDto);
    when(cardServiceClient.getActiveUserCardByAccountNumber(anyString()))
        .thenReturn(cardResponseDto);

    // when
    List<EventEntity> actualResult =
        underTest.createEvents(transactionEntity, transactionCreateRequestDto);

    // then
    verify(eventRepository, times(1)).saveAll(anyList());
    verify(accountService, times(1))
        .replenishAccountBalance(anyString(), any(AccountReplenishByAccountNumberRequestDto.class));
    verify(accountService, times(1))
        .withdrawAccountBalance(anyString(), any(AccountWithdrawByAccountNumberRequestDto.class));

    assertEquals(EventStatus.DECLINED, actualResult.get(0).getStatus());
    assertEquals(EventStatus.DECLINED, actualResult.get(1).getStatus());
  }

  @Test
  void whenCreateEventsWithFee_thenSuccess() {
    // given
    eventsList.get(2).setType(Type.OUTCOME);
    when(accountService.replenishAccountBalance(
            anyString(), any(AccountReplenishByAccountNumberRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(accountService.withdrawAccountBalance(
            anyString(), any(AccountWithdrawByAccountNumberRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(eventRepository.saveAll(anyList())).thenReturn(eventsList);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(accountService.getDetailedAccountInfo(anyString())).thenReturn(accountDetailedResponseDto);
    when(cardServiceClient.getActiveUserCardByAccountNumber(anyString()))
        .thenReturn(cardResponseDto);

    // when
    List<EventEntity> actualResult =
        underTest.createEvents(transactionEntity, transactionCreateRequestDtoWithFee);

    // then
    verify(eventRepository, times(1)).saveAll(anyList());
    verify(accountService, times(2))
        .withdrawAccountBalance(anyString(), any(AccountWithdrawByAccountNumberRequestDto.class));
    verify(accountService, times(2))
        .replenishAccountBalance(anyString(), any(AccountReplenishByAccountNumberRequestDto.class));

    assertEquals(
        transactionCreateRequestDtoWithFee.accountNumberWithdrawn(),
        actualResult.get(0).getAccountNumber());
    assertEquals(
        transactionCreateRequestDtoWithFee.accountNumberReplenished(),
        actualResult.get(1).getAccountNumber());
    assertEquals(4, actualResult.size());
    assertEquals(EventStatus.APPROVED, actualResult.get(2).getStatus());
    assertEquals(EventStatus.APPROVED, actualResult.get(3).getStatus());
    assertEquals(eventsList.get(2).getType(), actualResult.get(2).getType());
    assertEquals(eventsList.get(3).getType(), actualResult.get(3).getType());
    assertEquals(transactionCreateRequestDtoWithFee.feeAmount(), actualResult.get(2).getAmount());
    assertEquals(transactionCreateRequestDtoWithFee.feeAmount(), actualResult.get(3).getAmount());
  }

  @Test
  void whenCreateEventsWithFee_thenFeeEventDeclined() {
    // given
    when(accountService.replenishAccountBalance(
            transactionCreateRequestDtoWithFee.accountNumberReplenished(),
            new AccountReplenishByAccountNumberRequestDto(BigDecimal.TEN)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(accountService.withdrawAccountBalance(
            transactionCreateRequestDtoWithFee.accountNumberWithdrawn(),
            new AccountWithdrawByAccountNumberRequestDto(BigDecimal.TEN)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(eventRepository.saveAll(anyList())).thenReturn(eventsList);
    when(accountService.withdrawAccountBalance(
            transactionCreateRequestDtoWithFee.accountNumberWithdrawn(),
            new AccountWithdrawByAccountNumberRequestDto(BigDecimal.ONE)))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.NOT_FOUND_ACCOUNT_BY_ID.formatted(
                    transactionCreateRequestDtoWithFee.accountNumberWithdrawn())));
    when(accountService.replenishAccountBalance(
            EventServiceImpl.BANK_ACCOUNT_NUMBER,
            new AccountReplenishByAccountNumberRequestDto(BigDecimal.ONE)))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.NOT_FOUND_ACCOUNT_BY_ID.formatted(
                    EventServiceImpl.BANK_ACCOUNT_NUMBER)));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(accountService.getDetailedAccountInfo(anyString())).thenReturn(accountDetailedResponseDto);
    when(cardServiceClient.getActiveUserCardByAccountNumber(anyString()))
        .thenReturn(cardResponseDto);

    // when
    List<EventEntity> actualResult =
        underTest.createEvents(transactionEntity, transactionCreateRequestDtoWithFee);

    // then
    verify(eventRepository, times(1)).saveAll(anyList());
    verify(accountService, times(2))
        .replenishAccountBalance(anyString(), any(AccountReplenishByAccountNumberRequestDto.class));
    verify(accountService, times(2))
        .withdrawAccountBalance(anyString(), any(AccountWithdrawByAccountNumberRequestDto.class));

    assertEquals(EventStatus.DECLINED, actualResult.get(2).getStatus());
    assertEquals(EventStatus.DECLINED, actualResult.get(3).getStatus());
  }

  @Test
  void whenCreateEventsWithLoanInterest_thenSuccess() {
    // given
    when(accountService.replenishAccountBalance(
            anyString(), any(AccountReplenishByAccountNumberRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(accountService.withdrawAccountBalance(
            anyString(), any(AccountWithdrawByAccountNumberRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(eventRepository.saveAll(anyList())).thenReturn(eventsList);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(accountService.getDetailedAccountInfo(anyString())).thenReturn(accountDetailedResponseDto);
    when(cardServiceClient.getActiveUserCardByAccountNumber(anyString()))
        .thenReturn(cardResponseDto);

    // when
    List<EventEntity> actualResult =
        underTest.createEvents(transactionEntity, transactionCreateRequestDtoWithLoanInterest);

    // then
    verify(eventRepository, times(1)).saveAll(anyList());
    verify(accountService, times(2))
        .withdrawAccountBalance(anyString(), any(AccountWithdrawByAccountNumberRequestDto.class));
    verify(accountService, times(2))
        .replenishAccountBalance(anyString(), any(AccountReplenishByAccountNumberRequestDto.class));

    assertEquals(
        transactionCreateRequestDtoWithLoanInterest.accountNumberWithdrawn(),
        actualResult.get(0).getAccountNumber());
    assertEquals(
        transactionCreateRequestDtoWithLoanInterest.accountNumberReplenished(),
        actualResult.get(1).getAccountNumber());
    assertEquals(4, actualResult.size());
    assertEquals(EventStatus.APPROVED, actualResult.get(2).getStatus());
    assertEquals(EventStatus.APPROVED, actualResult.get(3).getStatus());
    assertEquals(eventsList.get(2).getType(), actualResult.get(2).getType());
    assertEquals(eventsList.get(3).getType(), actualResult.get(3).getType());
    assertEquals(
        transactionCreateRequestDtoWithLoanInterest.loanInterestAmount(),
        actualResult.get(2).getAmount());
    assertEquals(
        transactionCreateRequestDtoWithLoanInterest.loanInterestAmount(),
        actualResult.get(3).getAmount());
  }

  @Test
  void whenCreateEventsWithLoanInterest_thenLoanEventsDeclined() {
    // given
    when(accountService.replenishAccountBalance(
            transactionCreateRequestDtoWithLoanInterest.accountNumberReplenished(),
            new AccountReplenishByAccountNumberRequestDto(BigDecimal.TEN)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(accountService.withdrawAccountBalance(
            transactionCreateRequestDtoWithLoanInterest.accountNumberWithdrawn(),
            new AccountWithdrawByAccountNumberRequestDto(BigDecimal.TEN)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.PAYMENT_SUCCEEDED));
    when(eventRepository.saveAll(anyList())).thenReturn(eventsList);
    when(accountService.withdrawAccountBalance(
            transactionCreateRequestDtoWithLoanInterest.accountNumberWithdrawn(),
            new AccountWithdrawByAccountNumberRequestDto(BigDecimal.ONE)))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.NOT_FOUND_ACCOUNT_BY_ID.formatted(
                    transactionCreateRequestDtoWithFee.accountNumberWithdrawn())));
    when(accountService.replenishAccountBalance(
            EventServiceImpl.BANK_ACCOUNT_NUMBER,
            new AccountReplenishByAccountNumberRequestDto(BigDecimal.ONE)))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.NOT_FOUND_ACCOUNT_BY_ID.formatted(
                    EventServiceImpl.BANK_ACCOUNT_NUMBER)));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    when(accountService.getDetailedAccountInfo(anyString())).thenReturn(accountDetailedResponseDto);
    when(cardServiceClient.getActiveUserCardByAccountNumber(anyString()))
        .thenReturn(cardResponseDto);

    // when
    List<EventEntity> actualResult =
        underTest.createEvents(transactionEntity, transactionCreateRequestDtoWithLoanInterest);

    // then
    verify(eventRepository, times(1)).saveAll(anyList());
    verify(accountService, times(2))
        .replenishAccountBalance(anyString(), any(AccountReplenishByAccountNumberRequestDto.class));
    verify(accountService, times(2))
        .withdrawAccountBalance(anyString(), any(AccountWithdrawByAccountNumberRequestDto.class));

    assertEquals(EventStatus.DECLINED, actualResult.get(2).getStatus());
    assertEquals(EventStatus.DECLINED, actualResult.get(3).getStatus());
  }

  @Test
  void whenCheckEventsStatusIfEventsApproved_thenReturnFalse() {
    // given
    boolean expectedResult = false;

    // when
    boolean actualResult =
        underTest.checkEventsStatus(
            List.of(EventEntity.builder().status(EventStatus.APPROVED).build()));

    // then
    assertEquals(expectedResult, actualResult);
  }

  @Test
  void whenCheckEventsStatusIfEventsDeclined_thenReturnTrue() {
    // given
    boolean expectedResult = true;
    eventEntity.setStatus(EventStatus.DECLINED);

    // when
    boolean actualResult = underTest.checkEventsStatus(List.of(eventEntity));

    // then
    assertEquals(expectedResult, actualResult);
  }
}
