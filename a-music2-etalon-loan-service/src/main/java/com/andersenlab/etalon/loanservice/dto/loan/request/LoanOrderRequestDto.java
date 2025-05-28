package com.andersenlab.etalon.loanservice.dto.loan.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder
public record LoanOrderRequestDto(
    @Schema(example = "1") Long productId,
    @Schema(example = "Jan Kowalski")
        @NotBlank(message = "First and last name of borrower cannot be blank or null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in name")
        String borrower,
    @Schema(example = "6000.25") BigDecimal amount,
    @Schema(example = "1") Integer duration,
    @Schema(example = "6000.35") BigDecimal averageMonthlySalary,
    @Schema(example = "1000") BigDecimal averageMonthlyExpenses,
    @Schema @Valid Set<GuarantorRequestDto> guarantors) {}
