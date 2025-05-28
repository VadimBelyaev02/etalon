package com.andersenlab.etalon.loanservice.dto.loan.request;

import static com.andersenlab.etalon.loanservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.loanservice.util.Constants.BLANK_ACCOUNT_NUMBER;
import static com.andersenlab.etalon.loanservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(toBuilder = true)
public record CollectLoanRequestDto(
    @Schema(example = "1") Long loanOrderId,
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String accountNumber) {}
