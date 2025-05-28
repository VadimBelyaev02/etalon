package com.andersenlab.etalon.depositservice.dto.deposit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record MonthlyInterestIncomeDto(
    @Schema(example = "1") Long id,
    @Schema(example = "5 July 2023") String periodStart,
    @Schema(example = "5 August 2023") String periodEnd,
    @Schema(example = "1000") BigDecimal income,
    @Schema(example = "PL04234567840000000000000002") String interestAccountNumber) {}
