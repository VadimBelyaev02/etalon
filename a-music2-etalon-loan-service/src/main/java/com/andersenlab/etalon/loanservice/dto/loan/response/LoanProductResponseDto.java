package com.andersenlab.etalon.loanservice.dto.loan.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record LoanProductResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Cash loan") String name,
    @Schema(example = "5") Integer duration,
    @Schema(example = "12.12") BigDecimal apr,
    @Schema(example = "1") Integer requiredGuarantors,
    @Schema(example = "5000.00") BigDecimal minAmount,
    @Schema(example = "20000.00") BigDecimal maxAmount,
    @Schema(example = "250.00") BigDecimal monthlyCommission) {}
