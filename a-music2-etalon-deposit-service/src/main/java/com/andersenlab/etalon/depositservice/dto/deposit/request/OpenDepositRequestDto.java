package com.andersenlab.etalon.depositservice.dto.deposit.request;

import static com.andersenlab.etalon.depositservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.depositservice.util.Constants.BLANK_SOURCE;
import static com.andersenlab.etalon.depositservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record OpenDepositRequestDto(
    @Schema(example = "1") Long depositProductId,
    @Schema(example = "1000.25") BigDecimal depositAmount,
    @Schema(example = "12") Integer depositPeriod,
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = BLANK_SOURCE)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String sourceAccount,
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String interestAccount,
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String finalTransferAccount) {}
