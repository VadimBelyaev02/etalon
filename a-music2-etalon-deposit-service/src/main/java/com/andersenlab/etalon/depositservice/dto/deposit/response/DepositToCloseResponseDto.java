package com.andersenlab.etalon.depositservice.dto.deposit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record DepositToCloseResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "6000") BigDecimal balance,
    @Schema(example = "PL04234567840000000000000001") String finalTransferAccountNumber,
    @Schema(example = "Profit") String depositProductName) {}
