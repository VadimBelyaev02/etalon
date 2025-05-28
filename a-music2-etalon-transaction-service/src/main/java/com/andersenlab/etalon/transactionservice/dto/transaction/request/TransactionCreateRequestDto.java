package com.andersenlab.etalon.transactionservice.dto.transaction.request;

import static com.andersenlab.etalon.transactionservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.transactionservice.util.Constants.BLANK_ACCOUNT_NUMBER;
import static com.andersenlab.etalon.transactionservice.util.Constants.INVALID_AMOUNT;
import static com.andersenlab.etalon.transactionservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransactionCreateRequestDto(
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        String accountNumberWithdrawn,
    @Schema(example = "100")
        @NotNull
        @Min(value = 1, message = INVALID_AMOUNT)
        @Max(value = 100_000, message = INVALID_AMOUNT)
        BigDecimal amount,
    @Schema(example = "100")
        @NotNull
        @Min(value = 1, message = INVALID_AMOUNT)
        @Max(value = 100_000, message = INVALID_AMOUNT)
        BigDecimal loanInterestAmount,
    @Schema(anyOf = CurrencyName.class) CurrencyName currency,
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        String accountNumberReplenished,
    String transactionName,
    @Schema(anyOf = Details.class) Details details,
    Boolean isFeeProvided,
    BigDecimal standardRate,
    BigDecimal feeAmount,
    BigDecimal loanPenaltyAmount) {}
