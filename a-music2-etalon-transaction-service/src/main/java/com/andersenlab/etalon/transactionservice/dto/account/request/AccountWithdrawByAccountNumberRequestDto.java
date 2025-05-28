package com.andersenlab.etalon.transactionservice.dto.account.request;

import static com.andersenlab.etalon.transactionservice.util.Constants.INVALID_AMOUNT;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record AccountWithdrawByAccountNumberRequestDto(
    @Schema(example = "100")
        @Max(value = 100_000, message = INVALID_AMOUNT)
        @Min(value = 1, message = INVALID_AMOUNT)
        BigDecimal withdrawAmount) {}
