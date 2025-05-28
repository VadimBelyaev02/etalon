package com.andersenlab.etalon.transactionservice.dto.transaction.request;

import static com.andersenlab.etalon.transactionservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.transactionservice.util.Constants.BLANK_BENEFICIARY;
import static com.andersenlab.etalon.transactionservice.util.Constants.BLANK_SOURCE;
import static com.andersenlab.etalon.transactionservice.util.Constants.INVALID_AMOUNT;
import static com.andersenlab.etalon.transactionservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateTransferRequestDto(
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_SOURCE)
        String sender,
    @Schema(example = "PL04234567840000000000000002")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_BENEFICIARY)
        String beneficiary,
    @Schema(example = "100")
        @Max(value = 100_000, message = INVALID_AMOUNT)
        @Min(value = 1, message = INVALID_AMOUNT)
        BigDecimal amount,
    @Schema(example = "Payment for Gas") String description) {}
