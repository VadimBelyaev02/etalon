package com.andersenlab.etalon.loanservice.dto.loan.response;

import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoanResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "6000.23") BigDecimal amount,
    @Schema(example = "250.12") BigDecimal nextPaymentAmount,
    @Schema(example = "2023-07-12T10:23:54") ZonedDateTime nextPaymentDate,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "ACTIVE") LoanStatus status,
    @Schema(example = "Cash loan") String productName,
    @Schema(example = "5") Integer duration,
    @Schema(example = "2023-07-12T10:23:54") ZonedDateTime createdAt,
    @Schema(example = "3") Long productId) {}
