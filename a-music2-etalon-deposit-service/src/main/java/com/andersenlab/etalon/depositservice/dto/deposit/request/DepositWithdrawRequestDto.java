package com.andersenlab.etalon.depositservice.dto.deposit.request;

import static com.andersenlab.etalon.depositservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.depositservice.util.Constants.BLANK_ACCOUNT_NUMBER;
import static com.andersenlab.etalon.depositservice.util.Constants.INVALID_AMOUNT;
import static com.andersenlab.etalon.depositservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record DepositWithdrawRequestDto(
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String targetAccountNumber,
    @Schema(example = "1000.0") @Min(value = 1, message = INVALID_AMOUNT)
        BigDecimal withdrawAmount) {}
