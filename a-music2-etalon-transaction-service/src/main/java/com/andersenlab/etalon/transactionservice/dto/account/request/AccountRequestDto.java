package com.andersenlab.etalon.transactionservice.dto.account.request;

import static com.andersenlab.etalon.transactionservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.transactionservice.util.Constants.BLANK_SOURCE;
import static com.andersenlab.etalon.transactionservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountRequestDto(
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_SOURCE)
        String accountNumber,
    Boolean isBlocked,
    String status) {}
