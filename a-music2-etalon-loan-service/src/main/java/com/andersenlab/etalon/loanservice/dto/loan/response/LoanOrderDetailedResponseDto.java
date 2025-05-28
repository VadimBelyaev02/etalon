package com.andersenlab.etalon.loanservice.dto.loan.response;

import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;

@Builder
public record LoanOrderDetailedResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Dorota Kwasniewska") String borrower,
    @Schema(example = "Cash loan") String productName,
    @Schema(example = "5") Integer duration,
    @Schema(example = "5000.00") BigDecimal amount,
    @Schema(example = "9.50") BigDecimal apr,
    @Schema(example = "APPROVED") OrderStatus status,
    @Schema Set<GuarantorResponseDto> guarantors) {}
