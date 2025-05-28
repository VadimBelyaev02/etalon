package com.andersenlab.etalon.depositservice.dto.deposit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record DepositInterestDto(
    @Schema(example = "500") BigDecimal interestAmount,
    @Schema(example = "2024-06-16T14:26:52Z") ZonedDateTime createAt) {}
