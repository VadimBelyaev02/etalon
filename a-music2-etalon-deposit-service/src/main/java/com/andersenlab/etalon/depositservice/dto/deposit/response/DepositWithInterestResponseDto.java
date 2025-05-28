package com.andersenlab.etalon.depositservice.dto.deposit.response;

import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record DepositWithInterestResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "ACTIVE") DepositStatus status,
    @Schema(example = "7") BigDecimal interestRate) {}
