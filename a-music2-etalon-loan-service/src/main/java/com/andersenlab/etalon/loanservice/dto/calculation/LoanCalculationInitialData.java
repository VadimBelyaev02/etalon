package com.andersenlab.etalon.loanservice.dto.calculation;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoanCalculationInitialData(
    @Schema(example = "6000.25") BigDecimal amount,
    BigDecimal apr,
    BigDecimal monthlyCommission,
    ZonedDateTime createAt,
    Integer duration) {}
