package com.andersenlab.etalon.cardservice.dto.card.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CardDetailsRequestDto(
    @NotNull(message = "Invalid amount of withdraw limit")
        @Max(value = 200_000, message = "Invalid amount of withdraw limit")
        @Min(value = 1, message = "Invalid amount of withdraw limit")
        BigDecimal withdrawLimit,
    @NotNull(message = "Invalid amount of transfer limit")
        @Max(value = 100_000, message = "Invalid amount of transfer limit")
        @Min(value = 1, message = "Invalid amount of transfer limit")
        BigDecimal transferLimit,
    @NotNull(message = "Invalid amount of daily expense limit")
        @Max(value = 100_000, message = "Invalid amount of daily expense limit")
        @Min(value = 1, message = "Invalid amount of daily expense limit")
        BigDecimal dailyExpenseLimit) {}
