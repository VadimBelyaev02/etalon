package com.andersenlab.etalon.loanservice;

import com.andersenlab.etalon.loanservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.ActiveLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.CollectLoanRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.DelinquentLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.GuarantorRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.GuarantorResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanResponseDto;
import com.andersenlab.etalon.loanservice.entity.GuarantorEntity;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class MockData {

  public static LoanEntity getValidLoanEntity() {
    return new LoanEntity()
        .toBuilder()
            .id(1L)
            .userId("user")
            .amount(new BigDecimal("6000.0"))
            .contractNumber("1")
            .nextPaymentDate(
                ZonedDateTime.now(ZoneId.of("UTC")).minusMonths(1).truncatedTo(ChronoUnit.DAYS))
            .status(LoanStatus.ACTIVE)
            .accountNumber("PL04234567840000000000000001")
            .status(LoanStatus.ACTIVE)
            .product(getValidLoanProductEntity())
            .build();
  }

  public static LoanProductEntity getValidLoanProductEntity() {
    return new LoanProductEntity()
        .toBuilder()
            .id(2L)
            .name("Cash loan")
            .duration(2)
            .apr(new BigDecimal("12.83"))
            .requiredGuarantors(1)
            .minAmount(new BigDecimal("5000.0"))
            .maxAmount(new BigDecimal("15000.0"))
            .monthlyCommission(new BigDecimal("0"))
            .build();
  }

  public static LoanOrderEntity getValidLoanOrderEntity() {
    return LoanOrderEntity.builder()
        .id(1L)
        .userId("user")
        .borrower("USER TEST")
        .amount(new BigDecimal("6000.65"))
        .status(OrderStatus.APPROVED)
        .averageMonthlySalary(BigDecimal.valueOf(6000))
        .averageMonthlyExpenses(BigDecimal.valueOf(1000))
        .product(getValidLoanProductEntity())
        .guarantors(Set.of(new GuarantorEntity(1L, "11111111111", "Marta", "Nowak")))
        .build();
  }

  public static LoanResponseDto getValidLoanResponseDto() {
    return LoanResponseDto.builder()
        .id(1L)
        .amount(new BigDecimal("6000.0"))
        .nextPaymentAmount(new BigDecimal("282.50"))
        .nextPaymentDate(
            ZonedDateTime.now(ZoneId.of("UTC")).minusDays(4).truncatedTo(ChronoUnit.DAYS))
        .accountNumber("PL04234567840000000000000001")
        .status(LoanStatus.ACTIVE)
        .productName("Cash loan")
        .duration(2)
        .productId(2L)
        .build();
  }

  public static List<LoanResponseDto> getUnorderedListOfLoanResponseDtos() {
    return Stream.of(
            LoanResponseDto.builder()
                .id(1L)
                .status(LoanStatus.ACTIVE)
                .createdAt(ZonedDateTime.now().minusDays(64))
                .build(),
            LoanResponseDto.builder()
                .id(2L)
                .status(LoanStatus.DELINQUENT)
                .createdAt(ZonedDateTime.now().minusDays(100))
                .build(),
            LoanResponseDto.builder()
                .id(3L)
                .status(LoanStatus.ACTIVE)
                .createdAt(ZonedDateTime.now().minusDays(30))
                .build(),
            LoanResponseDto.builder()
                .id(4L)
                .status(LoanStatus.CLOSED)
                .createdAt(ZonedDateTime.now().minusDays(20))
                .build())
        .toList();
  }

  public static LoanOrderDetailedResponseDto getValidLoanOrderDetailedResponseDto() {
    return LoanOrderDetailedResponseDto.builder()
        .id(1L)
        .borrower("USER TEST")
        .productName("Cash loan")
        .duration(2)
        .amount(new BigDecimal("6000.65"))
        .apr(new BigDecimal("12.83"))
        .status(OrderStatus.APPROVED)
        .guarantors(Set.of(new GuarantorResponseDto(1L, "11111111111", "Marta", "Nowak")))
        .build();
  }

  public static LoanOrderRequestDto getValidLoanOrderRequestDto() {
    return LoanOrderRequestDto.builder()
        .productId(2L)
        .borrower("Jan Kowalski")
        .amount(BigDecimal.valueOf(6000))
        .duration(2)
        .averageMonthlySalary(BigDecimal.valueOf(6000))
        .averageMonthlyExpenses(BigDecimal.valueOf(1000))
        .guarantors(Set.of(getValidGuarantorRequestDto()))
        .build();
  }

  public static AccountResponseDto getValidAccountResponseDto() {
    return AccountResponseDto.builder()
        .id(1L)
        .iban("PL04234567840000000000000001")
        .balance(BigDecimal.valueOf(6000))
        .build();
  }

  public static AccountCreationRequestDto getValidAccountCreationRequestDto() {
    return AccountCreationRequestDto.builder().userId("user").type("LOAN").build();
  }

  public static GuarantorRequestDto getValidGuarantorRequestDto() {
    return GuarantorRequestDto.builder()
        .pesel("11111111111")
        .firstName("Marta")
        .lastName("Nowak")
        .build();
  }

  public static CollectLoanRequestDto getValidLoanRequestDto() {
    return CollectLoanRequestDto.builder()
        .loanOrderId(2L)
        .accountNumber("PL04234567840000000000000001")
        .build();
  }

  public static AccountDetailedResponseDto getValidAccountDetailedResponseDto() {
    return AccountDetailedResponseDto.builder()
        .id(1L)
        .userId("user")
        .iban("PL04234567840000000000000001")
        .isBlocked(false)
        .balance(BigDecimal.valueOf(6000))
        .build();
  }

  public static ActiveLoanPaymentRequestDto getValidActiveLoanPaymentRequestDto() {
    return ActiveLoanPaymentRequestDto.builder()
        .paymentAccountNumber("PL04234567840000000000000001")
        .totalPaymentAmount(BigDecimal.valueOf(282.08))
        .build();
  }

  public static DelinquentLoanPaymentRequestDto getValidDelinquentLoanPaymentRequestDto() {
    return DelinquentLoanPaymentRequestDto.builder()
        .paymentAccountNumber("PL04234567840000000000000001")
        .principalPaymentAmount(BigDecimal.valueOf(250.00))
        .accruedInterest(BigDecimal.valueOf(20.00))
        .accruedCommission(BigDecimal.valueOf(5.00))
        .penalty(BigDecimal.valueOf(7.08))
        .totalPaymentAmount(BigDecimal.valueOf(282.08))
        .build();
  }
}
