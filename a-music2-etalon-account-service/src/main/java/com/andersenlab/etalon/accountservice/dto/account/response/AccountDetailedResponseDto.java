package com.andersenlab.etalon.accountservice.dto.account.response;

import com.andersenlab.etalon.accountservice.util.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountDetailedResponseDto(
    Long id,
    @Schema(example = "PL**********************0172") String iban,
    String userId,
    @Schema(example = "1424.11") BigDecimal balance,
    @Schema(example = "false") Boolean isBlocked,
    @Schema(example = "PLN") Currency currency) {}
