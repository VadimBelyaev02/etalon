package com.andersenlab.etalon.depositservice.dto.transaction.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record EventResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "APPROVED") String status,
    @Schema(example = "INCOME") String type,
    @Schema(example = "100") BigDecimal amount,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "2024-06-16T14:26:52Z") String createAt,
    @Schema(example = "transaction name") String name) {}
