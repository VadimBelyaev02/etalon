package com.andersenlab.etalon.transactionservice.dto.template.request;

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
public record TemplatePatchRequestDto(
    Long productId,
    @Schema(example = "Transfer to another account") String templateName,
    String description,
    @Schema(example = "100")
        @Min(value = 1, message = INVALID_AMOUNT)
        @Max(value = 100000, message = INVALID_AMOUNT)
        BigDecimal amount,
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = BLANK_SOURCE)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String source,
    @Schema(example = "PL04234567840000000000000002")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_BENEFICIARY)
        String destination) {}
