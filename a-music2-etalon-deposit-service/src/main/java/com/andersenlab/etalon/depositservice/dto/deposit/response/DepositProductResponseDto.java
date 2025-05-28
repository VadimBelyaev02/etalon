package com.andersenlab.etalon.depositservice.dto.deposit.response;

import com.andersenlab.etalon.depositservice.util.enums.Currency;
import com.andersenlab.etalon.depositservice.util.enums.Term;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record DepositProductResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Profit") String name,
    @Schema(example = "1") BigDecimal minDepositPeriod,
    @Schema(example = "2") BigDecimal maxDepositPeriod,
    @Schema(example = "YEAR") Term term,
    @Schema(example = "PLN") Currency currency,
    @Schema(example = "9") BigDecimal interestRate,
    @Schema(example = "500") BigDecimal minOpenAmount,
    @Schema(example = "500000") BigDecimal maxDepositAmount,
    @Schema(example = "false") Boolean isEarlyWithdrawal,
    @Schema(example = "2024-06-16T14:26:52Z") ZonedDateTime createAt,
    @Schema(example = "2024-06-16T14:27:52Z") ZonedDateTime updateAt) {}
