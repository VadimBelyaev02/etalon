package com.andersenlab.etalon.transactionservice.dto.account.request;

import static com.andersenlab.etalon.transactionservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.transactionservice.util.Constants.INVALID_AMOUNT;
import static com.andersenlab.etalon.transactionservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AccountReplenishRequestDto(
    @Schema(example = "100")
        @Max(value = 100_000, message = INVALID_AMOUNT)
        @Min(value = 1, message = INVALID_AMOUNT)
        BigDecimal withdrawAmount,
    @Schema(example = "100")
        @Max(value = 100_000, message = INVALID_AMOUNT)
        @Min(value = 1, message = INVALID_AMOUNT)
        BigDecimal replenishAmount,
    @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String accountNumberToWithdraw) {}
