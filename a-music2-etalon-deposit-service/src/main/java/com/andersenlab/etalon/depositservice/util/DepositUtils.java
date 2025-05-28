package com.andersenlab.etalon.depositservice.util;

import static com.andersenlab.etalon.depositservice.util.Constants.DEPOSIT_INTEREST_HISTORY_DATE_FORMAT;

import com.andersenlab.etalon.depositservice.config.properties.PaginationProperties;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositInterestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositWithInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.MonthlyInterestIncomeDto;
import com.andersenlab.etalon.depositservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositInterestEntity;
import com.andersenlab.etalon.depositservice.util.enums.Details;
import com.andersenlab.etalon.depositservice.util.enums.Term;
import com.andersenlab.etalon.depositservice.util.filter.CustomPageRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class DepositUtils {

  public static ZonedDateTime calculateDepositEndDate(
      ZonedDateTime createdAt, Integer duration, Term term) {
    if (term.equals(Term.MONTH)) {
      return createdAt.plusMonths(duration);
    } else {
      return createdAt.plusYears(duration);
    }
  }

  public static BigDecimal calculateDailyDepositIncome(
      List<EventResponseDto> eventResponseDtoList,
      DepositWithInterestResponseDto depositWithInterestResponseDto,
      AccountBalanceResponseDto accountBalanceResponseDto) {

    BigDecimal interestCalculationBalance;
    BigDecimal accountBalance = accountBalanceResponseDto.accountBalance();
    BigDecimal interestRate = depositWithInterestResponseDto.interestRate();

    if (eventResponseDtoList.isEmpty()) {
      return calcIncome(accountBalance, interestRate, 1);
    }

    interestCalculationBalance =
        accountBalance
            .subtract(
                eventResponseDtoList.stream()
                    .filter(d -> d.type().equals("INCOME"))
                    .map(EventResponseDto::amount)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO))
            .add(
                eventResponseDtoList.stream()
                    .filter(d -> d.type().equals("OUTCOME"))
                    .map(EventResponseDto::amount)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO));

    return calcIncome(interestCalculationBalance, interestRate, 1);
  }

  public static List<MonthlyInterestIncomeDto> getDepositInterestHistory(
      List<DepositInterestDto> depositInterestDtoList, DepositEntity entity, ZoneId zoneId) {
    List<MonthlyInterestIncomeDto> monthlyInterestIncomeList = new ArrayList<>();
    BigDecimal amount = BigDecimal.ZERO;
    ZonedDateTime firstDate = depositInterestDtoList.get(0).createAt();
    long id = 1L;

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(DEPOSIT_INTEREST_HISTORY_DATE_FORMAT).withZone(zoneId);

    for (DepositInterestDto dto : depositInterestDtoList) {
      if (getDayOfMonthFromZonedDateTime(dto.createAt())
              .compareTo(getDayOfMonthFromZonedDateTime(firstDate.minusDays(1)))
          == 0) {
        amount = amount.add(dto.interestAmount());
        monthlyInterestIncomeList.add(
            MonthlyInterestIncomeDto.builder()
                .id(id++)
                .income(amount)
                .interestAccountNumber(entity.getInterestAccountNumber())
                .periodStart(formatter.format(subtractMonth(dto.createAt())))
                .periodEnd(formatter.format(dto.createAt()))
                .build());
        amount = BigDecimal.ZERO;
      } else {
        amount = amount.add(dto.interestAmount());
      }
    }

    return monthlyInterestIncomeList;
  }

  public static BigDecimal calcIncome(BigDecimal amount, BigDecimal interestRate, long diff) {
    return amount
        .multiply(BigDecimal.valueOf(diff))
        .multiply(interestRate)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
        .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
  }

  public static ZonedDateTime subtractMonth(ZonedDateTime date) {
    return date.minusMonths(1);
  }

  public static Integer getDayOfMonthFromZonedDateTime(ZonedDateTime date) {
    return date.getDayOfMonth();
  }

  public static Boolean isDepositExpired(DepositEntity depositEntity, ZonedDateTime now) {
    Term term = depositEntity.getProduct().getTerm();
    ZonedDateTime expiresAt;
    if (term.equals(Term.MONTH)) {
      expiresAt = depositEntity.getCreateAt().plusMonths(depositEntity.getDuration());
    } else {
      expiresAt = depositEntity.getCreateAt().plusYears(depositEntity.getDuration());
    }
    return expiresAt.isBefore(now);
  }

  public static List<TransactionCreateRequestDto> calculateMonthlyIncome(
      List<DepositEntity> depositsToPay, List<DepositInterestEntity> depositInterestEntityList) {
    List<TransactionCreateRequestDto> transactionCreateRequestDtoList = new ArrayList<>();

    depositsToPay.forEach(
        d ->
            transactionCreateRequestDtoList.add(
                TransactionCreateRequestDto.builder()
                    .accountNumberReplenished(d.getInterestAccountNumber())
                    .accountNumberWithdrawn(DepositConstants.BANK_IBAN)
                    .amount(getCurrentMonthInterestAmount(depositInterestEntityList, d))
                    .details(Details.DEPOSIT_INTEREST)
                    .isFeeProvided(false)
                    .transactionName(
                        String.format(
                            DepositConstants.INTEREST_TRANSACTION_TEXT, d.getProduct().getName()))
                    .build()));
    return transactionCreateRequestDtoList;
  }

  public static BigDecimal getCurrentMonthInterestAmount(
      List<DepositInterestEntity> depositInterestEntityList, DepositEntity d) {
    return depositInterestEntityList.stream()
        .filter(interest -> interest.getDepositId().equals(d.getId()))
        .map(DepositInterestEntity::getInterestAmount)
        .filter(ObjectUtils::isNotEmpty)
        .filter(amount -> !BigDecimal.ZERO.equals(amount))
        .reduce(BigDecimal::add)
        .orElse(BigDecimal.ZERO);
  }

  public static PageRequest getPageRequestFromRequest(
      CustomPageRequest pageRequest, PaginationProperties paginationProperties) {
    String orderBy =
        StringUtils.isEmpty(pageRequest.orderBy())
            ? paginationProperties.getDefaultOrderBy()
            : pageRequest.orderBy();
    String sortBy =
        StringUtils.isEmpty(pageRequest.sortBy())
            ? paginationProperties.getDefaultSortBy()
            : pageRequest.sortBy();
    Integer pageNo =
        ObjectUtils.isEmpty(pageRequest.pageNo())
            ? paginationProperties.getDefaultPageNumber()
            : pageRequest.pageNo();
    Integer pageSize =
        ObjectUtils.isEmpty(pageRequest.pageSize())
            ? paginationProperties.getDefaultPageSize()
            : pageRequest.pageSize();

    return PageRequest.of(
        pageNo,
        pageSize,
        orderBy.equalsIgnoreCase(paginationProperties.getDefaultOrderBy())
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending());
  }
}
