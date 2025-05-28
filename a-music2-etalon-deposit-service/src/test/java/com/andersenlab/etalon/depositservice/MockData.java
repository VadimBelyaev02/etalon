package com.andersenlab.etalon.depositservice;

import static com.andersenlab.etalon.depositservice.util.Constants.DEPOSIT_INTEREST_HISTORY_DATE_FORMAT;

import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositWithdrawRequestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositInterestDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositWithInterestResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.MonthlyInterestIncomeDto;
import com.andersenlab.etalon.depositservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import com.andersenlab.etalon.depositservice.util.enums.Currency;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import com.andersenlab.etalon.depositservice.util.enums.Term;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MockData {

  public static DepositProductEntity getValidDepositProductEntity() {
    return new DepositProductEntity()
        .toBuilder()
            .id(2L)
            .name("Looong")
            .minDepositPeriod(new BigDecimal(6))
            .maxDepositPeriod(new BigDecimal(24))
            .term(Term.MONTH)
            .minOpenAmount(new BigDecimal(500))
            .maxDepositAmount(new BigDecimal(500000))
            .currency(Currency.PLN)
            .interestRate(new BigDecimal(7))
            .isEarlyWithdrawal(true)
            .build();
  }

  public static DepositEntity getValidDepositEntity() {
    return new DepositEntity()
        .toBuilder()
            .id(1L)
            .duration(5)
            .accountNumber("1")
            .status(DepositStatus.ACTIVE)
            .userId("1")
            .product(
                new DepositProductEntity()
                    .toBuilder()
                        .id(1L)
                        .minDepositPeriod(BigDecimal.ONE)
                        .maxDepositPeriod(BigDecimal.ONE)
                        .minOpenAmount(BigDecimal.ONE)
                        .maxDepositAmount(BigDecimal.ONE)
                        .currency(Currency.PLN)
                        .interestRate(BigDecimal.valueOf(1))
                        .isEarlyWithdrawal(true)
                        .name("Looong")
                        .term(Term.MONTH)
                        .build())
            .interestAccountNumber("PL000")
            .createAt(ZonedDateTime.parse("2023-09-10T10:00:00Z"))
            .finalTransferAccountNumber("PL000")
            .build();
  }

  public static DepositWithdrawRequestDto getValidDepositWithdrawRequestDto() {
    return DepositWithdrawRequestDto.builder()
        .withdrawAmount(BigDecimal.valueOf(500.0))
        .targetAccountNumber("PL04234567840000000000000001")
        .build();
  }

  public static DepositReplenishRequestDto getValidDepositReplenishRequestDto() {
    return DepositReplenishRequestDto.builder()
        .replenishAmount(BigDecimal.valueOf(500))
        .withdrawalAccountNumber("PL04234567840000000000000001")
        .build();
  }

  public static List<MonthlyInterestIncomeDto> getValidMonthlyPaymentDtoList() {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(DEPOSIT_INTEREST_HISTORY_DATE_FORMAT)
            .withZone(ZoneId.systemDefault());
    return List.of(
        MonthlyInterestIncomeDto.builder()
            .id(1L)
            .income(BigDecimal.valueOf(120))
            .periodStart(formatter.format(ZonedDateTime.parse("2023-09-05T10:00:00Z")))
            .periodEnd(formatter.format(ZonedDateTime.parse("2023-10-05T10:00:00Z")))
            .interestAccountNumber("PL000")
            .build(),
        MonthlyInterestIncomeDto.builder()
            .id(2L)
            .income(BigDecimal.valueOf(1010))
            .periodStart(formatter.format(ZonedDateTime.parse("2023-10-05T10:00:00Z")))
            .periodEnd(formatter.format(ZonedDateTime.parse("2023-11-05T10:00:00Z")))
            .interestAccountNumber("PL000")
            .build());
  }

  public static List<EventResponseDto> getValidDepositEventResponseDtoList() {
    return List.of(
        EventResponseDto.builder()
            .status("ACCEPTED")
            .name("description")
            .amount(BigDecimal.TEN)
            .type("INCOME")
            .accountNumber("1")
            .createAt("2023-09-05T10:00:00Z")
            .build(),
        EventResponseDto.builder()
            .status("ACCEPTED")
            .name("description")
            .amount(BigDecimal.valueOf(100))
            .type("INCOME")
            .accountNumber("1")
            .createAt("2023-09-05T11:00:00Z")
            .build(),
        EventResponseDto.builder()
            .status("ACCEPTED")
            .name("description")
            .amount(BigDecimal.valueOf(100))
            .type("OUTCOME")
            .accountNumber("1")
            .createAt("2023-09-05T12:00:00Z")
            .build());
  }

  public static List<DepositInterestDto> getValidDepositInterestDtoList() {
    return List.of(
        DepositInterestDto.builder()
            .interestAmount(BigDecimal.TEN)
            .createAt(ZonedDateTime.parse("2023-09-06T10:00:00Z"))
            .build(),
        DepositInterestDto.builder()
            .interestAmount(BigDecimal.valueOf(100))
            .createAt(ZonedDateTime.parse("2023-09-08T10:00:00Z"))
            .build(),
        DepositInterestDto.builder()
            .interestAmount(BigDecimal.TEN)
            .createAt(ZonedDateTime.parse("2023-10-05T10:00:00Z"))
            .build(),
        DepositInterestDto.builder()
            .interestAmount(BigDecimal.valueOf(1000))
            .createAt(ZonedDateTime.parse("2023-10-07T10:00:00Z"))
            .build(),
        DepositInterestDto.builder()
            .interestAmount(BigDecimal.TEN)
            .createAt(ZonedDateTime.parse("2023-11-05T10:00:00Z"))
            .build());
  }

  public static DepositWithInterestResponseDto getValidDepositWithInterestResponseDto() {
    return DepositWithInterestResponseDto.builder()
        .id(1L)
        .accountNumber("1")
        .interestRate(BigDecimal.ONE)
        .build();
  }
}
