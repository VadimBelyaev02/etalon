package com.andersenlab.etalon.loanservice.dto.loan.request;

import static com.andersenlab.etalon.loanservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.loanservice.util.Constants.BLANK_ACCOUNT_NUMBER;
import static com.andersenlab.etalon.loanservice.util.Constants.INVALID_AMOUNT;
import static com.andersenlab.etalon.loanservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record DelinquentLoanPaymentRequestDto(
    @Schema(example = "PL04234567840000000000000001")
        @NotBlank(message = BLANK_ACCOUNT_NUMBER)
        @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String paymentAccountNumber,
    @Schema(example = "250.5")
        @NotNull(message = "Principal payment amount is required")
        @DecimalMin(value = "0.01", message = "Principal payment amount must be greater than zero")
        BigDecimal principalPaymentAmount,
    @Schema(example = "25")
        @NotNull(message = "Accrued interest is required")
        @DecimalMin(value = "0.00", message = "Accrued interest cannot be negative")
        BigDecimal accruedInterest,
    @Schema(example = "20")
        @NotNull(message = "Accrued commission is required")
        @DecimalMin(value = "0.00", message = "Accrued commission cannot be negative")
        BigDecimal accruedCommission,
    @Schema(example = "25")
        @NotNull(message = "Penalty is required")
        @DecimalMin(value = "0.00", message = "Penalty cannot be negative")
        BigDecimal penalty,
    @Schema(example = "320.5")
        @NotNull(message = "Total payment amount is required")
        @DecimalMin(value = "0.01", message = INVALID_AMOUNT)
        BigDecimal totalPaymentAmount)
    implements LoanPaymentRequestDto {}
