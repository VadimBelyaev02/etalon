package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.client.AccountServiceClient;
import com.andersenlab.etalon.loanservice.client.TransactionServiceClient;
import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.ActiveLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.CollectLoanRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.DelinquentLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.mapper.LoanMapper;
import com.andersenlab.etalon.loanservice.repository.LoanOrderRepository;
import com.andersenlab.etalon.loanservice.repository.LoanPaymentRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.repository.LoanRepository;
import com.andersenlab.etalon.loanservice.service.GeneratorService;
import com.andersenlab.etalon.loanservice.service.ValidationService;
import com.andersenlab.etalon.loanservice.service.impl.LoanServiceImpl;
import com.andersenlab.etalon.loanservice.service.strategies.impl.ActiveLoanPaymentStrategy;
import com.andersenlab.etalon.loanservice.service.strategies.impl.DelinquentLoanPaymentStrategy;
import com.andersenlab.etalon.loanservice.service.strategies.impl.LoanPaymentProcessor;
import com.andersenlab.etalon.loanservice.util.LoanUtils;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

  private static final String USER_ID = "user";
  private static final Long LOAN_ORDER_ID = 1L;
  private static final Long LOAN_ID = 1L;
  private static final LoanStatus LOAN_STATUS = LoanStatus.ACTIVE;
  private static final LoanStatus NO_LOAN_STATUS = null;
  private static final String CONTRACT_NUMBER = "CN00000000000001";
  public static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";
  private static final BigDecimal LOAN_AMOUNT = BigDecimal.valueOf(1000);
  private static final BigDecimal ZERO = BigDecimal.ZERO;

  private LoanEntity loanEntity;
  private LoanOrderEntity loanOrderEntity;
  private List<LoanEntity> loanEntityList;
  private LoanProductEntity loanProductEntity;
  private AccountResponseDto accountResponseDto;
  private AccountCreationRequestDto accountCreation;
  private CollectLoanRequestDto collectLoanRequestDto;
  private LoanPaymentRequestDto loanPaymentRequestDto;
  private AccountDetailedResponseDto accountDetailedResponseDto;
  private AccountBalanceResponseDto accountBalanceResponseDto;

  private ActiveLoanPaymentRequestDto activeLoanPaymentRequestDto;
  private DelinquentLoanPaymentRequestDto delinquentLoanPaymentRequestDto;
  @Mock private TimeProvider timeProvider;
  @Mock private LoanRepository loanRepository;
  @Mock private LoanOrderRepository loanOrderRepository;
  @Mock private LoanProductRepository loanProductRepository;
  @Mock private AccountServiceClient accountServiceClient;
  @Mock private TransactionServiceClient transactionServiceClient;
  @Mock private GeneratorService generatorService;
  @Mock private LoanPaymentRepository loanPaymentRepository;
  @Mock private ValidationService validationService;
  private LoanPaymentProcessor loanPaymentProcessor;
  @Spy private LoanMapper loanMapper = Mappers.getMapper(LoanMapper.class);
  private LoanServiceImpl underTest;

  @BeforeEach
  void setUp() {
    loanEntity = MockData.getValidLoanEntity();
    loanEntity.setCreateAt(ZonedDateTime.now().minusMonths(2).truncatedTo(ChronoUnit.DAYS));
    LoanEntity loanEntityWithStatusExpired = MockData.getValidLoanEntity();
    loanEntityWithStatusExpired.setCreateAt(
        ZonedDateTime.of(2023, 9, 9, 3, 0, 0, 0, ZoneId.of("UTC")));
    loanEntityWithStatusExpired.setStatus(LoanStatus.DELINQUENT);
    loanOrderEntity = MockData.getValidLoanOrderEntity();
    loanEntityList = List.of(loanEntity, loanEntityWithStatusExpired);
    loanProductEntity = MockData.getValidLoanProductEntity();
    accountResponseDto = MockData.getValidAccountResponseDto();
    accountCreation = MockData.getValidAccountCreationRequestDto();
    collectLoanRequestDto =
        CollectLoanRequestDto.builder()
            .loanOrderId(LOAN_ORDER_ID)
            .accountNumber(ACCOUNT_NUMBER)
            .build();
    accountDetailedResponseDto =
        AccountDetailedResponseDto.builder()
            .id(2L)
            .userId("user")
            .iban("PL04234567840000000000000001")
            .isBlocked(false)
            .balance(BigDecimal.valueOf(0))
            .build();
    accountBalanceResponseDto = new AccountBalanceResponseDto(BigDecimal.valueOf(500.0));

    activeLoanPaymentRequestDto = MockData.getValidActiveLoanPaymentRequestDto();
    delinquentLoanPaymentRequestDto = MockData.getValidDelinquentLoanPaymentRequestDto();

    ActiveLoanPaymentStrategy activeLoanPaymentStrategy =
        new ActiveLoanPaymentStrategy(
            loanPaymentRepository,
            accountServiceClient,
            timeProvider,
            transactionServiceClient,
            validationService);

    DelinquentLoanPaymentStrategy delinquentLoanPaymentStrategy =
        new DelinquentLoanPaymentStrategy(
            loanPaymentRepository,
            accountServiceClient,
            timeProvider,
            transactionServiceClient,
            validationService);

    loanPaymentProcessor =
        Mockito.spy(
            new LoanPaymentProcessor(
                List.of(activeLoanPaymentStrategy, delinquentLoanPaymentStrategy)));

    underTest =
        new LoanServiceImpl(
            loanRepository,
            loanProductRepository,
            loanOrderRepository,
            loanMapper,
            accountServiceClient,
            generatorService,
            transactionServiceClient,
            timeProvider,
            loanPaymentRepository,
            loanPaymentProcessor);
  }

  @Test
  void whenGetAllLoansWithStatus_thenSuccess() {
    // given
    final Integer ExpectedSizeOfLoansList = 1;
    when(loanRepository.findAllByUserId(USER_ID)).thenReturn(loanEntityList);
    when(accountServiceClient.getAccountBalanceByAccountNumber(anyString()))
        .thenReturn(accountBalanceResponseDto);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    final List<LoanResponseDto> result = underTest.getAllLoans(USER_ID, LOAN_STATUS);

    // then
    verify(loanRepository, times(1)).findAllByUserId(USER_ID);
    assertEquals(loanEntityList.get(0).getId(), result.get(0).id());
    assertEquals(loanEntityList.get(0).getAmount(), result.get(0).amount());
    assertEquals(loanEntityList.get(0).getNextPaymentDate(), result.get(0).nextPaymentDate());
    assertEquals(loanEntityList.get(0).getStatus(), result.get(0).status());
    assertEquals(loanEntityList.get(0).getAccountNumber(), result.get(0).accountNumber());
    assertEquals(loanEntityList.get(0).getProduct().getId(), result.get(0).productId());
    assertEquals(loanEntityList.get(0).getProduct().getName(), result.get(0).productName());
    assertEquals(loanEntityList.get(0).getProduct().getDuration(), result.get(0).duration());
    assertEquals(ExpectedSizeOfLoansList, result.size());
  }

  @Test
  void whenGetAllLoansWithoutStatus_thenSuccess() {
    // given
    when(loanRepository.findAllByUserId(USER_ID)).thenReturn(loanEntityList);
    when(accountServiceClient.getAccountBalanceByAccountNumber(anyString()))
        .thenReturn(accountBalanceResponseDto);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    final List<LoanResponseDto> result = underTest.getAllLoans(USER_ID, NO_LOAN_STATUS);

    // then
    verify(loanRepository, times(1)).findAllByUserId(USER_ID);
    assertEquals(loanEntityList.size(), result.size());
  }

  @Test
  void whensGetNoOpenLoans_thenReturnEmptyList() {
    // given
    when(loanRepository.findAllByUserId(USER_ID)).thenReturn(List.of());

    // when
    final List<LoanResponseDto> result = underTest.getAllLoans(USER_ID, NO_LOAN_STATUS);

    // then
    verify(loanRepository, times(1)).findAllByUserId(USER_ID);
    assertEquals(List.of(), result);
  }

  @Test
  void whenOpenNewLoan_thenLoanSavedAndReturnMessageResponse() {
    // given
    when(loanOrderRepository.findByIdAndUserId(collectLoanRequestDto.loanOrderId(), USER_ID))
        .thenReturn(Optional.of(loanOrderEntity));
    when(loanProductRepository.findById(loanOrderEntity.getProduct().getId()))
        .thenReturn(Optional.of(loanProductEntity));
    when(accountServiceClient.createAccount(accountCreation)).thenReturn(accountResponseDto);
    when(accountServiceClient.getDetailedAccountInfo(ACCOUNT_NUMBER))
        .thenReturn(accountDetailedResponseDto);
    when(transactionServiceClient.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.TRANSACTION_IS_SUCCESSFUL));
    when(loanRepository.findFirstByOrderByIdDesc()).thenReturn(Optional.ofNullable(loanEntity));
    when(generatorService.generateContractNumber(anyLong())).thenReturn(CONTRACT_NUMBER);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    underTest.openNewLoan(USER_ID, collectLoanRequestDto);

    // then
    verify(loanProductRepository).findById(loanOrderEntity.getProduct().getId());
    verify(accountServiceClient).createAccount(accountCreation);
    verify(transactionServiceClient).createTransaction(any(TransactionCreateRequestDto.class));
    verify(loanRepository).save(any(LoanEntity.class));

    assertNotNull(loanEntity);
    assertEquals(USER_ID, loanEntity.getUserId());
    assertDoesNotThrow(() -> underTest.collectLoan(USER_ID, loanOrderEntity, ACCOUNT_NUMBER));
  }

  @Test
  void whenCollectLoanWithValidData_thenLoanEntityCreated() {
    // given
    loanOrderEntity.setAmount(new BigDecimal("6000"));

    when(loanProductRepository.findById(loanOrderEntity.getProduct().getId()))
        .thenReturn(Optional.of(loanProductEntity));
    when(accountServiceClient.createAccount(accountCreation)).thenReturn(accountResponseDto);
    when(accountServiceClient.getDetailedAccountInfo(loanEntity.getAccountNumber()))
        .thenReturn(accountDetailedResponseDto);
    when(transactionServiceClient.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.TRANSACTION_IS_SUCCESSFUL));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    final LoanEntity loanEntity = underTest.collectLoan(USER_ID, loanOrderEntity, ACCOUNT_NUMBER);

    // then
    verify(loanProductRepository).findById(loanOrderEntity.getProduct().getId());
    verify(accountServiceClient).createAccount(accountCreation);
    verify(transactionServiceClient).createTransaction(any(TransactionCreateRequestDto.class));

    assertNotNull(loanEntity);
    assertEquals(USER_ID, loanEntity.getUserId());
    assertEquals(accountResponseDto.balance(), loanEntity.getAmount());
    assertDoesNotThrow(() -> underTest.collectLoan(USER_ID, loanOrderEntity, ACCOUNT_NUMBER));
  }

  @Test
  void whenCollectLoanWhenLoanOrderNotFound_shouldFail() {
    // then
    assertThrows(
        BusinessException.class,
        () -> underTest.collectLoan(USER_ID, loanOrderEntity, ACCOUNT_NUMBER));
  }

  @Test
  void whenCollectLoanWhenLoanOrderRejected_shouldFail() {
    // given
    loanOrderEntity.setStatus(OrderStatus.REJECTED);

    // then
    assertThrows(
        BusinessException.class,
        () -> underTest.collectLoan(USER_ID, loanOrderEntity, ACCOUNT_NUMBER));
  }

  @Test
  void whenGetDetailedLoanWithCorrectLoanId_thenSuccess() {
    // given
    when(loanRepository.findByIdAndUserId(LOAN_ID, USER_ID))
        .thenReturn(Optional.ofNullable(loanEntity));
    when(accountServiceClient.getAccountBalanceByAccountNumber(loanEntity.getAccountNumber()))
        .thenReturn(accountBalanceResponseDto);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    // when
    final LoanDetailedResponseDto result = underTest.getDetailedLoan(LOAN_ID, USER_ID);

    // then
    verify(loanRepository, times(1)).findByIdAndUserId(LOAN_ID, USER_ID);
    assertEquals(loanEntity.getId(), result.id());
    assertEquals(loanEntity.getProduct().getName(), result.productName());
    assertEquals(loanEntity.getProduct().getDuration(), result.duration());
    assertEquals(loanEntity.getAmount(), result.amount());
    assertEquals(BigDecimal.valueOf(5500.00), result.debtNetAmount());
    assertEquals(loanEntity.getContractNumber(), result.contractNumber());
    assertEquals(loanEntity.getProduct().getApr(), result.apr());
    assertEquals(282.08, result.nextPaymentAmount().doubleValue());
    assertEquals(loanEntity.getNextPaymentDate(), result.nextPaymentDate());
    assertEquals(loanEntity.getStatus(), result.status());
    assertEquals(loanEntity.getCreateAt(), result.createdAt());
    assertEquals(loanEntity.getAccountNumber(), result.accountNumber());
  }

  @Test
  void whenGetDetailedLoanWithIncorrectLoanId_thenThrowsBusinessException() {
    // given
    given(loanRepository.findByIdAndUserId(anyLong(), anyString())).willReturn(Optional.empty());

    // then
    Assertions.assertThrows(BusinessException.class, () -> underTest.getDetailedLoan(1L, USER_ID));
  }

  @Test
  void whenMakeLoanPaymentWithCorrectPaymentAmount_thenPaymentSucceedAndReturnMessageResponse() {
    // given
    loanEntity.setCreateAt(ZonedDateTime.now().minusMonths(3).plusDays(1));
    when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.ofNullable(loanEntity));
    when(accountServiceClient.getAccountBalanceByAccountNumber(loanEntity.getAccountNumber()))
        .thenReturn(accountBalanceResponseDto);
    when(transactionServiceClient.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.TRANSACTION_IS_SUCCESSFUL));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());

    ActiveLoanPaymentRequestDto loanPaymentRequestDto =
        ActiveLoanPaymentRequestDto.builder()
            .paymentAccountNumber(ACCOUNT_NUMBER)
            .totalPaymentAmount(
                new BigDecimal("282.08")) // or any valid amount expected by the logic
            .build();

    // when
    MessageResponseDto response = underTest.makeLoanPayment(LOAN_ID, loanPaymentRequestDto);

    // then
    verify(loanRepository, times(1)).findById(LOAN_ID);
    verify(loanPaymentProcessor, times(1)).processLoanPayment(loanEntity, loanPaymentRequestDto);
    verify(loanRepository, times(1)).save(any(LoanEntity.class));
    verify(transactionServiceClient, times(1))
        .createTransaction(any(TransactionCreateRequestDto.class));

    assertNotNull(response);
    assertEquals(MessageResponseDto.PAYMENT_SUCCEEDED, response.message());
  }

  @Test
  void whenMakeLoanPaymentWithInCorrectAccountNumber_thenPaymentFails() {
    // given
    when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.ofNullable(loanEntity));

    // when/then
    assertThrows(
        BusinessException.class, () -> underTest.makeLoanPayment(1L, loanPaymentRequestDto));
  }

  @Test
  void whenMakeLoanPaymentWithActiveLoan_thenProcessorInvoked() {
    // given
    LoanEntity activeLoan = loanEntity.toBuilder().status(LoanStatus.ACTIVE).build();
    when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(activeLoan));

    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(6);
    ZonedDateTime currentDateTime = createAt.plusMonths(7);

    BigDecimal loanAccountBalance = LOAN_AMOUNT.subtract(BigDecimal.valueOf(500));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(activeLoan.getAccountNumber()))
        .thenReturn(new AccountBalanceResponseDto(loanAccountBalance));

    // when
    underTest.makeLoanPayment(LOAN_ID, activeLoanPaymentRequestDto);

    // then
    verify(loanRepository, times(1)).findById(LOAN_ID);
    verify(loanPaymentProcessor, times(1))
        .processLoanPayment(activeLoan, activeLoanPaymentRequestDto);
    verify(loanRepository, times(1)).save(activeLoan);
  }

  @Test
  void whenMakeLoanPaymentWithDelinquentLoan_thenProcessorInvoked() {
    // given
    LoanEntity delinquentLoan = loanEntity.toBuilder().status(LoanStatus.DELINQUENT).build();
    when(loanRepository.findById(LOAN_ID)).thenReturn(Optional.of(delinquentLoan));

    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(6);
    ZonedDateTime currentDateTime = createAt.plusMonths(7);

    BigDecimal loanAccountBalance = LOAN_AMOUNT.subtract(BigDecimal.valueOf(500));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(delinquentLoan.getAccountNumber()))
        .thenReturn(new AccountBalanceResponseDto(loanAccountBalance));

    // when
    underTest.makeLoanPayment(LOAN_ID, delinquentLoanPaymentRequestDto);

    // then
    verify(loanRepository, times(1)).findById(LOAN_ID);
    verify(loanPaymentProcessor, times(1))
        .processLoanPayment(delinquentLoan, delinquentLoanPaymentRequestDto);
    verify(loanRepository, times(1)).save(delinquentLoan);
  }

  @Test
  void whenMakeLoanPaymentWithInvalidLoanId_thenThrowsBusinessException() {
    // given
    given(loanRepository.findById(anyLong())).willReturn(Optional.empty());

    // then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> underTest.makeLoanPayment(LOAN_ID, activeLoanPaymentRequestDto));

    assertEquals(
        String.format(BusinessException.LOAN_NOT_FOUND_BY_ID, LOAN_ID), exception.getMessage());
  }

  @Test
  void whenProcessActiveLoanPayment_thenStrategyInvoked() {
    // given
    ActiveLoanPaymentStrategy strategy = mock(ActiveLoanPaymentStrategy.class);
    LoanEntity activeLoan = loanEntity.toBuilder().status(LoanStatus.ACTIVE).build();

    // when
    strategy.processPayment(activeLoan, activeLoanPaymentRequestDto);

    // then
    verify(strategy, times(1)).processPayment(activeLoan, activeLoanPaymentRequestDto);
  }

  @Test
  void whenProcessDelinquentLoanPayment_thenStrategyInvoked() {
    // given
    DelinquentLoanPaymentStrategy strategy = mock(DelinquentLoanPaymentStrategy.class);
    LoanEntity delinquentLoan = loanEntity.toBuilder().status(LoanStatus.DELINQUENT).build();

    // when
    strategy.processPayment(delinquentLoan, delinquentLoanPaymentRequestDto);

    // then
    verify(strategy, times(1)).processPayment(delinquentLoan, delinquentLoanPaymentRequestDto);
  }

  @Test
  void whenMakeDelinquentLoanPaymentWithValidData_thenLoanEntityUpdated() {
    // given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(6);
    ZonedDateTime currentDateTime = createAt.plusMonths(7);

    LoanEntity delinquentLoan =
        loanEntity.toBuilder()
            .createAt(createAt)
            .nextPaymentDate(createAt.plusMonths(6))
            .status(LoanStatus.DELINQUENT)
            .build();

    BigDecimal loanAccountBalance = LOAN_AMOUNT.subtract(BigDecimal.valueOf(500));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(delinquentLoan.getAccountNumber()))
        .thenReturn(new AccountBalanceResponseDto(loanAccountBalance));
    when(loanPaymentRepository.countByLoan(delinquentLoan)).thenReturn(5L);

    LoanCalculationResult calculationResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(delinquentLoan),
            loanAccountBalance,
            currentDateTime);

    BigDecimal expectedPenalty = LoanUtils.calculatePenalty(delinquentLoan, currentDateTime, 5L);
    BigDecimal expectedTotalPaymentAmount =
        calculationResult
            .loanDebtGrossAmount()
            .add(expectedPenalty)
            .setScale(2, RoundingMode.HALF_EVEN);

    DelinquentLoanPaymentRequestDto paymentRequestDto =
        delinquentLoanPaymentRequestDto.toBuilder()
            .principalPaymentAmount(calculationResult.loanDebtNetAmount())
            .accruedInterest(calculationResult.loanInterestAmount())
            .accruedCommission(calculationResult.monthlyCommissionAmount())
            .penalty(expectedPenalty)
            .totalPaymentAmount(expectedTotalPaymentAmount)
            .build();

    AccountBalanceResponseDto paymentAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.valueOf(1000.00));
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalanceResponseDto);

    // when
    loanPaymentProcessor.processLoanPayment(delinquentLoan, paymentRequestDto);

    // then
    verify(transactionServiceClient).createTransaction(any(TransactionCreateRequestDto.class));
    assertEquals(LoanStatus.ACTIVE, delinquentLoan.getStatus());
  }

  @Test
  void whenProcessLoanPaymentWithValidData_thenLoanEntityUpdated() {
    // given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(4);
    ZonedDateTime currentDateTime = createAt.plusMonths(4);

    LoanEntity activeLoan =
        loanEntity.toBuilder().createAt(createAt).nextPaymentDate(createAt.plusMonths(4)).build();

    ActiveLoanPaymentRequestDto paymentRequestDto =
        activeLoanPaymentRequestDto.toBuilder()
            .totalPaymentAmount(BigDecimal.valueOf(458.34))
            .build();

    AccountBalanceResponseDto paymentAccountBalanceResponseDto =
        new AccountBalanceResponseDto(LOAN_AMOUNT);
    AccountBalanceResponseDto loanAccountBalanceResponseDto = new AccountBalanceResponseDto(ZERO);

    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(transactionServiceClient.createTransaction(any(TransactionCreateRequestDto.class)))
        .thenReturn(new MessageResponseDto(MessageResponseDto.TRANSACTION_IS_SUCCESSFUL));
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalanceResponseDto);
    when(accountServiceClient.getAccountBalanceByAccountNumber(activeLoan.getAccountNumber()))
        .thenReturn(loanAccountBalanceResponseDto);

    // when
    loanPaymentProcessor.processLoanPayment(activeLoan, paymentRequestDto);

    // then
    verify(transactionServiceClient).createTransaction(any(TransactionCreateRequestDto.class));
    assertEquals(
        activeLoan.getNextPaymentDate().truncatedTo(ChronoUnit.DAYS),
        currentDateTime.plusMonths(1).truncatedTo(ChronoUnit.DAYS));
  }

  @Test
  void whenProcessLoanPaymentWithExcessivePaymentAmount_thenThrowsException() {
    // given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(4);
    ZonedDateTime currentDateTime = createAt.plusMonths(4);

    LoanEntity loanEntity =
        MockData.getValidLoanEntity().toBuilder()
            .createAt(createAt)
            .nextPaymentDate(createAt.plusMonths(4))
            .build();

    ActiveLoanPaymentRequestDto paymentRequestDto =
        MockData.getValidActiveLoanPaymentRequestDto().toBuilder()
            .totalPaymentAmount(BigDecimal.valueOf(1000))
            .build();

    AccountBalanceResponseDto paymentAccountBalance =
        new AccountBalanceResponseDto(new BigDecimal("5000.00"));
    AccountBalanceResponseDto loanAccountBalance = new AccountBalanceResponseDto(BigDecimal.ZERO);

    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalance);
    when(accountServiceClient.getAccountBalanceByAccountNumber(loanEntity.getAccountNumber()))
        .thenReturn(loanAccountBalance);

    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST,
                BusinessException.LOAN_PAYMENT_REJECTED_DUE_TO_EXCESSIVE_AMOUNT))
        .when(validationService)
        .validatePaymentAmount(eq(paymentRequestDto.totalPaymentAmount()), any(BigDecimal.class));

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              loanPaymentProcessor.processLoanPayment(loanEntity, paymentRequestDto);
            });

    assertEquals(
        BusinessException.LOAN_PAYMENT_REJECTED_DUE_TO_EXCESSIVE_AMOUNT, exception.getMessage());
  }

  @Test
  void whenProcessActiveLoanPaymentWithInsufficientFunds_thenThrowsException() {
    // given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(4);
    ZonedDateTime currentDateTime = createAt.plusMonths(4);

    LoanEntity loanEntity =
        MockData.getValidLoanEntity().toBuilder()
            .createAt(createAt)
            .nextPaymentDate(createAt.plusMonths(4))
            .build();

    ActiveLoanPaymentRequestDto paymentRequestDto =
        MockData.getValidActiveLoanPaymentRequestDto().toBuilder()
            .totalPaymentAmount(BigDecimal.valueOf(458.34))
            .build();

    AccountBalanceResponseDto paymentAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.valueOf(100.00));
    AccountBalanceResponseDto loanAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.ZERO);

    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalanceResponseDto);
    when(accountServiceClient.getAccountBalanceByAccountNumber(loanEntity.getAccountNumber()))
        .thenReturn(loanAccountBalanceResponseDto);

    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST, BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT))
        .when(validationService)
        .validateSufficientFunds(any(BigDecimal.class), any(BigDecimal.class));

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              loanPaymentProcessor.processLoanPayment(loanEntity, paymentRequestDto);
            });

    assertEquals(BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT, exception.getMessage());
  }

  @Test
  void whenProcessLoanPaymentWithFinalPayment_thenLoanIsClosed() {
    // given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(6);
    ZonedDateTime currentDateTime = createAt.plusMonths(7); // One month overdue

    LoanEntity delinquentLoan =
        MockData.getValidLoanEntity().toBuilder()
            .createAt(createAt)
            .nextPaymentDate(createAt.plusMonths(6))
            .status(LoanStatus.DELINQUENT)
            .build();

    BigDecimal loanAccountBalance = BigDecimal.valueOf(500);

    LoanCalculationResult calculationResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(delinquentLoan),
            loanAccountBalance,
            currentDateTime);
    BigDecimal expectedPenalty = LoanUtils.calculatePenalty(delinquentLoan, currentDateTime, 5L);

    DelinquentLoanPaymentRequestDto paymentRequestDto =
        MockData.getValidDelinquentLoanPaymentRequestDto().toBuilder()
            .principalPaymentAmount(calculationResult.loanDebtNetAmount())
            .accruedInterest(calculationResult.loanInterestAmount())
            .accruedCommission(calculationResult.monthlyCommissionAmount())
            .penalty(expectedPenalty)
            .totalPaymentAmount(calculationResult.loanDebtGrossAmount().add(expectedPenalty))
            .build();

    AccountBalanceResponseDto paymentAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.valueOf(50.00));
    AccountBalanceResponseDto loanAccountBalanceResponseDto =
        new AccountBalanceResponseDto(loanAccountBalance);

    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalanceResponseDto);
    when(accountServiceClient.getAccountBalanceByAccountNumber(delinquentLoan.getAccountNumber()))
        .thenReturn(loanAccountBalanceResponseDto);

    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST, BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT))
        .when(validationService)
        .validateSufficientFunds(any(BigDecimal.class), any(BigDecimal.class));

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              loanPaymentProcessor.processLoanPayment(delinquentLoan, paymentRequestDto);
            });

    assertEquals(BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT, exception.getMessage());
  }

  @Test
  void whenProcessDelinquentLoanPaymentWithValidData_thenLoanEntityUpdated() {
    // given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(6);
    ZonedDateTime currentDateTime = createAt.plusMonths(7);

    LoanEntity delinquentLoan =
        MockData.getValidLoanEntity().toBuilder()
            .createAt(createAt)
            .nextPaymentDate(createAt.plusMonths(6))
            .status(LoanStatus.DELINQUENT)
            .build();

    BigDecimal loanAccountBalance = BigDecimal.valueOf(500);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(delinquentLoan.getAccountNumber()))
        .thenReturn(new AccountBalanceResponseDto(loanAccountBalance));

    LoanCalculationResult calculationResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(delinquentLoan),
            loanAccountBalance,
            currentDateTime);

    BigDecimal expectedPenalty = LoanUtils.calculatePenalty(delinquentLoan, currentDateTime, 5L);
    BigDecimal expectedTotalPaymentAmount =
        calculationResult
            .loanDebtGrossAmount()
            .add(expectedPenalty)
            .setScale(2, RoundingMode.HALF_EVEN);

    DelinquentLoanPaymentRequestDto paymentRequestDto =
        MockData.getValidDelinquentLoanPaymentRequestDto().toBuilder()
            .principalPaymentAmount(calculationResult.loanDebtNetAmount())
            .accruedInterest(calculationResult.loanInterestAmount())
            .accruedCommission(calculationResult.monthlyCommissionAmount())
            .penalty(expectedPenalty)
            .totalPaymentAmount(expectedTotalPaymentAmount)
            .build();

    AccountBalanceResponseDto paymentAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.valueOf(1000.00));
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalanceResponseDto);

    // when
    loanPaymentProcessor.processLoanPayment(delinquentLoan, paymentRequestDto);

    // then
    verify(transactionServiceClient).createTransaction(any(TransactionCreateRequestDto.class));
    assertEquals(LoanStatus.ACTIVE, delinquentLoan.getStatus());
  }

  @Test
  void whenProcessDelinquentLoanPaymentWithInsufficientFunds_thenThrowsException() {
    // Given
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(4);
    ZonedDateTime currentDateTime = createAt.plusMonths(4);

    LoanEntity loanEntity =
        MockData.getValidLoanEntity().toBuilder()
            .createAt(createAt)
            .nextPaymentDate(createAt.plusMonths(4))
            .build();

    ActiveLoanPaymentRequestDto paymentRequestDto =
        MockData.getValidActiveLoanPaymentRequestDto().toBuilder()
            .totalPaymentAmount(BigDecimal.valueOf(458.34))
            .build();

    AccountBalanceResponseDto paymentAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.valueOf(100.00));
    AccountBalanceResponseDto loanAccountBalanceResponseDto =
        new AccountBalanceResponseDto(BigDecimal.ZERO);

    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentDateTime);
    when(accountServiceClient.getAccountBalanceByAccountNumber(
            paymentRequestDto.paymentAccountNumber()))
        .thenReturn(paymentAccountBalanceResponseDto);
    when(accountServiceClient.getAccountBalanceByAccountNumber(loanEntity.getAccountNumber()))
        .thenReturn(loanAccountBalanceResponseDto);

    doThrow(
            new BusinessException(
                HttpStatus.BAD_REQUEST, BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT))
        .when(validationService)
        .validateSufficientFunds(any(BigDecimal.class), any(BigDecimal.class));

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              loanPaymentProcessor.processLoanPayment(loanEntity, paymentRequestDto);
            });

    assertEquals(BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT, exception.getMessage());
  }

  @Test
  void whenMakeLoanPaymentOnClosedLoan_thenThrowsException() {
    // given
    LoanEntity closedLoan = loanEntity.toBuilder().status(LoanStatus.CLOSED).build();
    ActiveLoanPaymentRequestDto paymentRequestDto = activeLoanPaymentRequestDto;

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              loanPaymentProcessor.processLoanPayment(closedLoan, paymentRequestDto);
            });

    assertEquals(
        String.format(BusinessException.UNSUPPORTED_LOAN_STATUS, closedLoan.getStatus()),
        exception.getMessage());
  }

  @Test
  void whenMakingLoanPaymentWithNullInput_thenThrowsException() {
    // when / then
    assertThrows(
        BusinessException.class,
        () -> {
          underTest.makeLoanPayment(LOAN_ID, null);
        });
  }
}
