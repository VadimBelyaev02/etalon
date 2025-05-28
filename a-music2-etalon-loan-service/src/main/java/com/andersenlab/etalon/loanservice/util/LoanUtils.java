package com.andersenlab.etalon.loanservice.util;

import static com.andersenlab.etalon.loanservice.util.Constants.DAILY_PENALTY_RATE;
import static com.andersenlab.etalon.loanservice.util.Constants.FINAL_SCALE;
import static com.andersenlab.etalon.loanservice.util.Constants.INTERMEDIATE_SCALE;
import static com.andersenlab.etalon.loanservice.util.Constants.MONTHS_IN_YEAR;

import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationInitialData;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.LongStream;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class LoanUtils {
  private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100.00);

  public static LoanCalculationResult calculateLoanPayments(
      LoanCalculationInitialData loanCalculationInitialData,
      BigDecimal loanAccountBalance,
      ZonedDateTime now) {
    log.info("{calculateLoanPayments} -> Calculating loan payments");
    BigDecimal monthPaymentAmountNet =
        loanCalculationInitialData
            .amount()
            .divide(
                BigDecimal.valueOf((long) loanCalculationInitialData.duration() * MONTHS_IN_YEAR),
                4,
                RoundingMode.CEILING);
    Long monthsAlreadyPassed =
        ChronoUnit.MONTHS.between(loanCalculationInitialData.createAt(), now);
    BigDecimal shouldBeAlreadyPayedAmountNet =
        monthPaymentAmountNet.multiply(BigDecimal.valueOf(monthsAlreadyPassed));
    BigDecimal shouldBePayedThisMonthNet =
        shouldBeAlreadyPayedAmountNet.subtract(loanAccountBalance).add(monthPaymentAmountNet);

    BigDecimal shouldBePayedMonthlyCommissions = BigDecimal.ZERO;
    if (loanCalculationInitialData.monthlyCommission().compareTo(BigDecimal.ZERO) > 0) {
      BigDecimal monthCountToPayCommission =
          shouldBePayedThisMonthNet.divide(monthPaymentAmountNet, RoundingMode.CEILING);
      shouldBePayedMonthlyCommissions =
          loanCalculationInitialData.monthlyCommission().multiply(monthCountToPayCommission);
    }

    BigDecimal shouldBePayedThisMonthGross =
        shouldBePayedThisMonthNet
            .multiply(
                (loanCalculationInitialData.apr().add(ONE_HUNDRED))
                    .divide(ONE_HUNDRED, 4, RoundingMode.CEILING))
            .add(shouldBePayedMonthlyCommissions);
    return LoanCalculationResult.builder()
        .monthlyCommissionAmount(shouldBePayedMonthlyCommissions.setScale(2, RoundingMode.CEILING))
        .loanDebtNetAmount(shouldBePayedThisMonthNet.setScale(2, RoundingMode.CEILING))
        .loanInterestAmount(
            shouldBePayedThisMonthGross
                .subtract(shouldBePayedThisMonthNet)
                .subtract(shouldBePayedMonthlyCommissions)
                .setScale(2, RoundingMode.CEILING))
        .loanDebtGrossAmount(shouldBePayedThisMonthGross.setScale(2, RoundingMode.CEILING))
        .build();
  }

  public static LoanCalculationInitialData collectLoanCalculationInitialData(LoanEntity entity) {
    return LoanCalculationInitialData.builder()
        .apr(entity.getProduct().getApr())
        .duration(entity.getProduct().getDuration())
        .createAt(entity.getCreateAt())
        .amount(entity.getAmount())
        .monthlyCommission(entity.getProduct().getMonthlyCommission())
        .build();
  }

  public static BigDecimal calculatePenalty(
      LoanEntity loanEntity, ZonedDateTime now, long paymentsMade) {

    log.info("{calculatePenalty} -> Calculating penalty for loan ID {}", loanEntity.getId());

    ZonedDateTime loanStartDate = loanEntity.getCreateAt();
    ZonedDateTime nextPaymentDate = loanEntity.getNextPaymentDate();

    if (!now.isAfter(nextPaymentDate)) {
      log.info(
          "No penalty since current date {} is not after next payment date {}",
          now,
          nextPaymentDate);
      return BigDecimal.ZERO;
    }

    BigDecimal loanActualAmount = loanEntity.getAmount();
    int loanDurationMonths = loanEntity.getProduct().getDuration() * MONTHS_IN_YEAR;
    BigDecimal monthlyPaymentAmount =
        loanActualAmount.divide(
            BigDecimal.valueOf(loanDurationMonths), INTERMEDIATE_SCALE, RoundingMode.CEILING);

    long totalExpectedPayments = Math.max(ChronoUnit.MONTHS.between(loanStartDate, now), 1);
    long missedPayments = totalExpectedPayments - paymentsMade;

    if (missedPayments <= 0) {
      return BigDecimal.ZERO;
    }

    long totalOverdueDays =
        LongStream.range(0, missedPayments)
            .mapToObj(i -> loanStartDate.plusMonths(paymentsMade + i))
            .mapToLong(dueDate -> Math.max(ChronoUnit.DAYS.between(dueDate, now), 0))
            .sum();

    BigDecimal penalty =
        monthlyPaymentAmount
            .multiply(DAILY_PENALTY_RATE)
            .multiply(BigDecimal.valueOf(totalOverdueDays));

    return penalty.setScale(FINAL_SCALE, RoundingMode.CEILING);
  }
}
