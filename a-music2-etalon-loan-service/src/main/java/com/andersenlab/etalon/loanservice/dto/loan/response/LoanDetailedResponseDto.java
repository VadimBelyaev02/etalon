package com.andersenlab.etalon.loanservice.dto.loan.response;

import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoanDetailedResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Easy loan") String productName,
    @Schema(example = "1") Integer duration,
    @Schema(example = "6000") BigDecimal amount,
    @Schema(example = "6000") BigDecimal debtNetAmount,
    @Schema(example = "1000") BigDecimal shouldBePayedThisMonthNet,
    @Schema(example = "250") BigDecimal shouldBePayedThisMonthInterest,
    @Schema(example = "20") BigDecimal shouldBePayedThisMonthCommission,
    @Schema(example = "25") BigDecimal penalty,
    @Schema(example = "1") String contractNumber,
    @Schema(example = "12.12") BigDecimal apr,
    @Schema(example = "250.12") BigDecimal nextPaymentAmount,
    @Schema(example = "2023-07-12T10:23:54") ZonedDateTime nextPaymentDate,
    @Schema(example = "ACTIVE") LoanStatus status,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "2023-07-12T10:23:54") ZonedDateTime createdAt) {}
