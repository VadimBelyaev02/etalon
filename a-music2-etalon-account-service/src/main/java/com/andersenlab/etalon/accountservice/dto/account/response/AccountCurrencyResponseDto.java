package com.andersenlab.etalon.accountservice.dto.account.response;

import com.andersenlab.etalon.accountservice.util.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;

public record AccountCurrencyResponseDto(
    Long id,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "PLN") Currency currency) {}
