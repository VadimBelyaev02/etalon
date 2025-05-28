package com.andersenlab.etalon.accountservice.dto.account.response;

import com.andersenlab.etalon.accountservice.util.enums.AccountStatus;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import com.andersenlab.etalon.accountservice.util.enums.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountResponseDto(
    Long id,
    @Schema(example = "PL**********************0172") String iban,
    @Schema(example = "4523.22") BigDecimal balance,
    @Schema(example = "PLN") Currency currency,
    @Schema(example = "false") Boolean isBlocked,
    @Schema(example = "ACTIVE/EXPIRED/APPROVED") AccountStatus status,
    @Schema(example = "DEPOSIT/CARD/LOAN/PAYMENT/BANK") Type accountType) {}
