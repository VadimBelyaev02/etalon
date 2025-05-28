package com.andersenlab.etalon.loanservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.util.Constants;
import com.andersenlab.etalon.loanservice.util.LoanUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LoanUtilsTest {
  private LoanEntity entity;
  @Mock private TimeProvider timeProvider;

  @BeforeEach
  void setUp() {
    entity = MockData.getValidLoanEntity();
  }

  @Test
  void whenCalculateLoanPayments_thenCorrectDataReturned() {
    // given
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(ZonedDateTime.now());
    entity.setCreateAt(ZonedDateTime.now().minusMonths(2).truncatedTo(ChronoUnit.DAYS));
    BigDecimal loanAccountBalance = BigDecimal.valueOf(500.0);

    // when
    final LoanCalculationResult actualResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(entity),
            loanAccountBalance,
            timeProvider.getCurrentZonedDateTime());

    // then
    assertEquals(282.08, actualResult.loanDebtGrossAmount().doubleValue());
    assertEquals(250.0, actualResult.loanDebtNetAmount().doubleValue());
    assertEquals(32.08, actualResult.loanInterestAmount().doubleValue());
  }

  @Test
  void whenCalculateLoanPaymentsWithCreationDate28February_thenCorrectDataReturned() {
    // given
    entity.setCreateAt(ZonedDateTime.of(2023, 2, 28, 10, 30, 0, 0, ZoneId.of("UTC")));

    BigDecimal loanAccountBalance = BigDecimal.valueOf(250.0);
    when(timeProvider.getCurrentZonedDateTime())
        .thenReturn(ZonedDateTime.of(2023, 9, 28, 10, 30, 0, 0, ZoneId.of("UTC")));

    // when
    final LoanCalculationResult actualResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(entity),
            loanAccountBalance,
            timeProvider.getCurrentZonedDateTime());

    // then
    assertEquals(1974.53, actualResult.loanDebtGrossAmount().doubleValue());
    assertEquals(1750.0, actualResult.loanDebtNetAmount().doubleValue());
    assertEquals(224.53, actualResult.loanInterestAmount().doubleValue());
  }

  @Test
  void whenCalculatePenaltyWithNoOverdue_thenReturnZero() {
    // given
    LoanEntity loanEntity = MockData.getValidLoanEntity();
    ZonedDateTime now = loanEntity.getNextPaymentDate().minusDays(1);
    long paymentsMade = 1L;

    // when
    BigDecimal penalty = LoanUtils.calculatePenalty(loanEntity, now, paymentsMade);

    // then
    assertEquals(BigDecimal.ZERO, penalty);
  }

  @Test
  void whenCalculatePenaltyWithOverdue_thenReturnCorrectPenalty() {
    // given
    LoanEntity loanEntity = MockData.getValidLoanEntity();
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(3);
    loanEntity.setCreateAt(createAt);
    loanEntity.setNextPaymentDate(createAt.plusMonths(3));

    ZonedDateTime now = createAt.plusMonths(4);
    long paymentsMade = 2L;

    BigDecimal loanAmount = loanEntity.getAmount();
    int loanDurationMonths = loanEntity.getProduct().getDuration() * Constants.MONTHS_IN_YEAR;
    BigDecimal monthlyPaymentAmount =
        loanAmount.divide(
            BigDecimal.valueOf(loanDurationMonths),
            Constants.INTERMEDIATE_SCALE,
            RoundingMode.CEILING);

    long totalExpectedPayments = Math.max(ChronoUnit.MONTHS.between(createAt, now), 1);
    long missedPayments = totalExpectedPayments - paymentsMade;

    long totalOverdueDays =
        LongStream.range(0, missedPayments)
            .mapToObj(i -> createAt.plusMonths(paymentsMade + i))
            .mapToLong(dueDate -> Math.max(ChronoUnit.DAYS.between(dueDate, now), 0))
            .sum();

    BigDecimal expectedPenalty =
        monthlyPaymentAmount
            .multiply(Constants.DAILY_PENALTY_RATE)
            .multiply(BigDecimal.valueOf(totalOverdueDays))
            .setScale(Constants.FINAL_SCALE, RoundingMode.CEILING);

    // when
    BigDecimal penalty = LoanUtils.calculatePenalty(loanEntity, now, paymentsMade);

    // then
    assertEquals(expectedPenalty, penalty);
  }

  @Test
  void whenCalculatePenaltyWithMultipleMissedPayments_thenReturnCorrectPenalty() {
    // given
    LoanEntity loanEntity = MockData.getValidLoanEntity();
    ZonedDateTime createAt = ZonedDateTime.now().minusMonths(6);
    loanEntity.setCreateAt(createAt);
    loanEntity.setNextPaymentDate(createAt.plusMonths(6));

    ZonedDateTime now = createAt.plusMonths(8);
    long paymentsMade = 4L;

    BigDecimal loanAmount = loanEntity.getAmount();
    int loanDurationMonths = loanEntity.getProduct().getDuration() * Constants.MONTHS_IN_YEAR;
    BigDecimal monthlyPaymentAmount =
        loanAmount.divide(
            BigDecimal.valueOf(loanDurationMonths),
            Constants.INTERMEDIATE_SCALE,
            RoundingMode.CEILING);

    long totalExpectedPayments = Math.max(ChronoUnit.MONTHS.between(createAt, now), 1);
    long missedPayments = totalExpectedPayments - paymentsMade;

    long totalOverdueDays =
        LongStream.range(0, missedPayments)
            .mapToObj(i -> createAt.plusMonths(paymentsMade + i))
            .mapToLong(dueDate -> Math.max(ChronoUnit.DAYS.between(dueDate, now), 0))
            .sum();

    BigDecimal expectedPenalty =
        monthlyPaymentAmount
            .multiply(Constants.DAILY_PENALTY_RATE)
            .multiply(BigDecimal.valueOf(totalOverdueDays))
            .setScale(Constants.FINAL_SCALE, RoundingMode.CEILING);

    // when
    BigDecimal penalty = LoanUtils.calculatePenalty(loanEntity, now, paymentsMade);

    // then
    assertEquals(expectedPenalty, penalty);
  }

  @Test
  void whenCalculatePenaltyWithPaymentsUpToDate_thenReturnZero() {
    // given
    LoanEntity loanEntity = MockData.getValidLoanEntity();
    ZonedDateTime now = loanEntity.getNextPaymentDate();
    long paymentsMade = 1L;

    // when
    BigDecimal penalty = LoanUtils.calculatePenalty(loanEntity, now, paymentsMade);

    // then
    assertEquals(BigDecimal.ZERO, penalty);
  }
}
