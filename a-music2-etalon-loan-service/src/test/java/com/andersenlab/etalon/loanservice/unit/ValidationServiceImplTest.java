package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.client.AccountServiceClient;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.dto.loan.request.DelinquentLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.entity.GuarantorEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.exception.ValidationException;
import com.andersenlab.etalon.loanservice.mapper.GuarantorMapper;
import com.andersenlab.etalon.loanservice.repository.GuarantorRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.impl.ValidationServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

  private static final Long PRODUCT_ID = 2L;
  private static final BigDecimal AMOUNT = BigDecimal.valueOf(6000);
  private static final BigDecimal INVALID_AMOUNT = BigDecimal.valueOf(1000);
  private static final Integer DURATION = 2;
  private static final Integer INVALID_DURATION = 1;
  private static final BigDecimal AVERAGE_MONTHLY_SALARY = BigDecimal.valueOf(6000);
  private static final BigDecimal INVALID_AVERAGE_MONTHLY_SALARY = BigDecimal.valueOf(500);
  private static final BigDecimal AVERAGE_MONTHLY_EXPENSES = BigDecimal.valueOf(1000);

  private LoanProductEntity loanProductEntity;
  private LoanOrderRequestDto loanOrderRequestDto;

  @Mock private LoanProductRepository loanProductRepository;
  @Mock private GuarantorRepository guarantorRepository;
  @Mock private AccountServiceClient accountServiceClient;
  @Spy private GuarantorMapper guarantorMapper = Mappers.getMapper(GuarantorMapper.class);
  @InjectMocks private ValidationServiceImpl validationService;

  @BeforeEach
  void setUp() {
    loanProductEntity = MockData.getValidLoanProductEntity();
    loanOrderRequestDto = MockData.getValidLoanOrderRequestDto();
  }

  @Test
  void whenValidateLoanRequestDto_shouldSuccess() {
    // given
    loanOrderRequestDto = MockData.getValidLoanOrderRequestDto();

    // when
    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));

    // then
    assertDoesNotThrow(() -> validationService.validateLoanOrderRequestDto(loanOrderRequestDto));
    verify(loanProductRepository).findById(loanOrderRequestDto.productId());
  }

  @Test
  void whenValidateLoanRequestDtoWithInvalidAmount_shouldFail() {
    // given
    loanOrderRequestDto =
        LoanOrderRequestDto.builder()
            .productId(PRODUCT_ID)
            .amount(INVALID_AMOUNT)
            .duration(DURATION)
            .averageMonthlySalary(AVERAGE_MONTHLY_SALARY)
            .averageMonthlyExpenses(AVERAGE_MONTHLY_EXPENSES)
            .build();

    // when
    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));

    // then
    assertThrows(
        ValidationException.class,
        () -> validationService.validateLoanOrderRequestDto(loanOrderRequestDto));
  }

  @Test
  void whenValidateLoanRequestDtoWithIncorrectDuration_shouldFail() {
    // given
    loanOrderRequestDto =
        LoanOrderRequestDto.builder()
            .productId(PRODUCT_ID)
            .amount(AMOUNT)
            .duration(INVALID_DURATION)
            .averageMonthlySalary(AVERAGE_MONTHLY_SALARY)
            .averageMonthlyExpenses(AVERAGE_MONTHLY_EXPENSES)
            .build();

    // when
    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));

    // then
    assertThrows(
        ValidationException.class,
        () -> validationService.validateLoanOrderRequestDto(loanOrderRequestDto));
  }

  @Test
  void whenValidateLoanRequestDtoWithSalaryLessThanExpenses_shouldFail() {
    loanOrderRequestDto =
        LoanOrderRequestDto.builder()
            .productId(PRODUCT_ID)
            .amount(AMOUNT)
            .duration(DURATION)
            .averageMonthlySalary(INVALID_AVERAGE_MONTHLY_SALARY)
            .averageMonthlyExpenses(AVERAGE_MONTHLY_EXPENSES)
            .build();

    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));

    assertThrows(
        ValidationException.class,
        () -> validationService.validateLoanOrderRequestDto(loanOrderRequestDto));
  }

  @Test
  void whenDtoGuarantorsIsNull_shouldFailValidateLoanOrderRequestDto() {
    loanOrderRequestDto =
        LoanOrderRequestDto.builder()
            .productId(PRODUCT_ID)
            .amount(AMOUNT)
            .duration(DURATION)
            .averageMonthlySalary(AVERAGE_MONTHLY_SALARY)
            .averageMonthlyExpenses(AVERAGE_MONTHLY_EXPENSES)
            .guarantors(null)
            .build();

    when(loanProductRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(loanProductEntity));

    assertThrows(
        ValidationException.class,
        () -> validationService.validateLoanOrderRequestDto(loanOrderRequestDto));
  }

  @Test
  void whenValidateGuarantors_ShouldSuccess() {
    Set<GuarantorEntity> guarantorEntities =
        guarantorMapper.setOfDtosToEntities(loanOrderRequestDto.guarantors());

    when(guarantorRepository.findAllByPeselIn(anySet())).thenReturn(guarantorEntities);

    assertDoesNotThrow(() -> validationService.validateGuarantors(loanOrderRequestDto));

    verify(guarantorRepository, times(1)).findAllByPeselIn(anySet());
  }

  @Test
  void whenValidateGuarantors_ShouldFailWhenNameMismatched() {
    GuarantorEntity existingGuarantor = new GuarantorEntity(1L, "11111111111", "Marta", "Kowalska");
    when(guarantorRepository.findAllByPeselIn(anySet()))
        .thenReturn(Collections.singleton(existingGuarantor));

    assertThrows(
        ValidationException.class, () -> validationService.validateGuarantors(loanOrderRequestDto));

    verify(guarantorRepository, times(1)).findAllByPeselIn(anySet());
  }

  @Test
  void whenValidatePaymentAccountWithValidAccount_thenSuccess() {
    // given
    String userId = "user";
    String paymentAccountNumber = "PL04234567840000000000000001";
    AccountDetailedResponseDto accountDetails = MockData.getValidAccountDetailedResponseDto();

    when(accountServiceClient.getDetailedAccountInfo(paymentAccountNumber))
        .thenReturn(accountDetails);

    // when / then
    assertDoesNotThrow(
        () -> validationService.validatePaymentAccount(userId, paymentAccountNumber));

    verify(accountServiceClient, times(1)).getDetailedAccountInfo(paymentAccountNumber);
  }

  @Test
  void whenValidatePaymentAccountWithInvalidUserId_thenThrowsException() {
    // given
    String userId = "anotherUser";
    String paymentAccountNumber = "PL04234567840000000000000001";
    AccountDetailedResponseDto accountDetails = MockData.getValidAccountDetailedResponseDto();

    when(accountServiceClient.getDetailedAccountInfo(paymentAccountNumber))
        .thenReturn(accountDetails);

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validatePaymentAccount(userId, paymentAccountNumber);
            });

    assertEquals(
        BusinessException.TRANSACTION_REJECTED_DUE_TO_INVALID_ACCOUNT_NUMBER,
        exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void whenValidatePaymentAccountWithBlockedAccount_thenThrowsException() {
    // given
    String userId = "user";
    String paymentAccountNumber = "PL04234567840000000000000001";
    AccountDetailedResponseDto accountDetails =
        MockData.getValidAccountDetailedResponseDto().toBuilder().isBlocked(true).build();

    when(accountServiceClient.getDetailedAccountInfo(paymentAccountNumber))
        .thenReturn(accountDetails);

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validatePaymentAccount(userId, paymentAccountNumber);
            });

    assertEquals(
        BusinessException.TRANSACTION_REJECTED_DUE_TO_ACCOUNT_BLOCKING, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void whenValidatePaymentAccountWithNonExistentAccount_thenThrowsException() {
    // given
    String userId = "user";
    String paymentAccountNumber = "PL123";

    when(accountServiceClient.getDetailedAccountInfo(paymentAccountNumber))
        .thenThrow(
            new BusinessException(
                HttpStatus.NOT_FOUND,
                BusinessException.TRANSACTION_REJECTED_DUE_TO_INVALID_ACCOUNT_NUMBER));

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validatePaymentAccount(userId, paymentAccountNumber);
            });

    assertEquals(
        BusinessException.TRANSACTION_REJECTED_DUE_TO_INVALID_ACCOUNT_NUMBER,
        exception.getMessage());
    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
  }

  @Test
  void whenValidatePaymentAmountWithExcessiveAmount_thenThrowsException() {
    // given
    BigDecimal paymentAmount = BigDecimal.valueOf(1000);
    BigDecimal expectedAmount = BigDecimal.valueOf(500);

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validatePaymentAmount(paymentAmount, expectedAmount);
            });

    assertEquals(
        BusinessException.LOAN_PAYMENT_REJECTED_DUE_TO_EXCESSIVE_AMOUNT, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void whenValidatePaymentAmountWithInsufficientAmount_thenThrowsException() {
    // given
    BigDecimal paymentAmount = BigDecimal.valueOf(300);
    BigDecimal expectedAmount = BigDecimal.valueOf(500);

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validatePaymentAmount(paymentAmount, expectedAmount);
            });

    assertEquals(
        BusinessException.LOAN_PAYMENT_REJECTED_DUE_TO_INSUFFICIENT_AMOUNT, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void whenValidatePaymentAmountWithExactAmount_thenSuccess() {
    // given
    BigDecimal paymentAmount = BigDecimal.valueOf(500);
    BigDecimal expectedAmount = BigDecimal.valueOf(500);

    // when / then
    assertDoesNotThrow(
        () -> validationService.validatePaymentAmount(paymentAmount, expectedAmount));
  }

  @Test
  void whenValidateSufficientFundsWithInsufficientFunds_thenThrowsException() {
    // given
    BigDecimal paymentAmount = BigDecimal.valueOf(600);
    BigDecimal accountBalance = BigDecimal.valueOf(500);

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validateSufficientFunds(paymentAmount, accountBalance);
            });

    assertEquals(BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT, exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
  }

  @Test
  void whenValidateSufficientFundsWithSufficientFunds_thenSuccess() {
    // given
    BigDecimal paymentAmount = BigDecimal.valueOf(500);
    BigDecimal accountBalance = BigDecimal.valueOf(600);

    // when / then
    assertDoesNotThrow(
        () -> validationService.validateSufficientFunds(paymentAmount, accountBalance));
  }

  @Test
  void whenValidatePaymentAmountsForDelinquentWithMismatchedAmounts_thenThrowsException() {
    // given
    DelinquentLoanPaymentRequestDto delinquentDto =
        MockData.getValidDelinquentLoanPaymentRequestDto();
    LoanCalculationResult calculationResult =
        LoanCalculationResult.builder()
            .loanDebtNetAmount(BigDecimal.valueOf(500))
            .loanInterestAmount(BigDecimal.valueOf(50))
            .monthlyCommissionAmount(BigDecimal.valueOf(10))
            .loanDebtGrossAmount(BigDecimal.valueOf(560))
            .build();
    BigDecimal expectedPenalty = BigDecimal.valueOf(20);

    DelinquentLoanPaymentRequestDto mismatchedDto =
        delinquentDto.toBuilder()
            .principalPaymentAmount(BigDecimal.valueOf(400)) // Should be 500
            .build();

    // when / then
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> {
              validationService.validatePaymentAmountsForDelinquent(
                  mismatchedDto, calculationResult, expectedPenalty);
            });

    assertEquals(BusinessException.PAYMENT_AMOUNT_HAS_CHANGED_TRY_AGAIN, exception.getMessage());
    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
  }

  @Test
  void whenValidatePaymentAmountsForDelinquentWithCorrectAmounts_thenSuccess() {
    // given
    DelinquentLoanPaymentRequestDto delinquentDto =
        MockData.getValidDelinquentLoanPaymentRequestDto();
    LoanCalculationResult calculationResult =
        LoanCalculationResult.builder()
            .loanDebtNetAmount(delinquentDto.principalPaymentAmount())
            .loanInterestAmount(delinquentDto.accruedInterest())
            .monthlyCommissionAmount(delinquentDto.accruedCommission())
            .loanDebtGrossAmount(
                delinquentDto
                    .principalPaymentAmount()
                    .add(delinquentDto.accruedInterest())
                    .add(delinquentDto.accruedCommission()))
            .build();
    BigDecimal expectedPenalty = delinquentDto.penalty();

    // when / then
    assertDoesNotThrow(
        () -> {
          validationService.validatePaymentAmountsForDelinquent(
              delinquentDto, calculationResult, expectedPenalty);
        });
  }
}
