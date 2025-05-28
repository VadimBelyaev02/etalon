package com.andersenlab.etalon.accountservice.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountInterestResponseDto(
    Long id,
    @Schema(example = "PL**********************0172") String accountNumber,
    @Schema(example = "2253.3") BigDecimal balance) {}
