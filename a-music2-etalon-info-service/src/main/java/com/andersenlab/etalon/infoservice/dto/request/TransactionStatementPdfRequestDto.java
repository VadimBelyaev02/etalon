package com.andersenlab.etalon.infoservice.dto.request;

import static com.andersenlab.etalon.infoservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.infoservice.util.Constants.BLANK_ACCOUNT_NUMBER;
import static com.andersenlab.etalon.infoservice.util.Constants.INVALID_AMOUNT;
import static com.andersenlab.etalon.infoservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import com.andersenlab.etalon.infoservice.util.enums.Currency;
import com.andersenlab.etalon.infoservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.infoservice.util.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record TransactionStatementPdfRequestDto(
    @NotBlank(message = "First name cannot be blank or null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in first name")
        @Schema(example = "Grzegorz")
        String firstName,
    @NotBlank(message = "Second name cannot be null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in second name")
        @Schema(example = "Bzeczyszczykiewicz")
        String lastName,
    String transactionDate,
    String transactionTime,
    String transactionName,
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        String outcomeAccountNumber,
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        String incomeAccountNumber,
    @Schema(example = "100")
        @Max(value = 100_000, message = INVALID_AMOUNT)
        @Min(value = 1, message = INVALID_AMOUNT)
        BigDecimal transactionAmount,
    Currency currency,
    Type type,
    TransactionStatus status) {}
