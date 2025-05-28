package com.andersenlab.etalon.depositservice.dto.deposit.response;

import com.andersenlab.etalon.depositservice.util.enums.Currency;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record DepositDetailedResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Profit") String productName,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "6000") BigDecimal actualAmount,
    @Schema(example = "PLN") Currency productCurrency,
    @Schema(example = "2023-07-12T10:23:54") ZonedDateTime validFrom,
    @Schema(example = "2023-08-12T10:23:54") ZonedDateTime validUntil,
    @Schema(example = "7") BigDecimal productInterestRate,
    @Schema(example = "ACTIVE") DepositStatus status,
    @Schema(example = "PL04234567840000000000000002") String interestAccountNumber,
    @Schema(example = "PL04234567840000000000000002") String finalTransferAccountNumber,
    @Schema(example = "true") Boolean isProductEarlyWithdrawal,
    @Schema List<MonthlyInterestIncomeDto> monthlyPayments) {}
