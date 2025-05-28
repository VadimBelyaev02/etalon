package com.andersenlab.etalon.loanservice.dto.loan.response;

import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record LoanOrderResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Easy loan") String productName,
    @Schema(example = "1") Integer duration,
    @Schema(example = "6000") BigDecimal amount,
    @Schema(example = "IN_REVIEW") OrderStatus status) {}
