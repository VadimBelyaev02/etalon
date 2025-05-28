package com.andersenlab.etalon.cardservice.dto.deposit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record DepositResponseDto(
    @Schema(example = "1") Long id,
    @JsonIgnore String accountNumber,
    @Schema(example = "100.5") BigDecimal actualAmount,
    @Schema(example = "12") Integer duration,
    @Schema(example = "2023-06-20T12:01:37Z") ZonedDateTime createdAt,
    @Schema(example = "2023-06-20T12:01:37Z") ZonedDateTime endDate,
    @Schema(example = "ACTIVE") DepositStatus status,
    @Schema(example = "1") Long depositProductId) {}
