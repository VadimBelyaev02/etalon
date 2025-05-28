package com.andersenlab.etalon.loanservice.dto.calculation;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record LoanCalculationResult(
    BigDecimal loanDebtNetAmount,
    BigDecimal monthlyCommissionAmount,
    BigDecimal loanInterestAmount,
    BigDecimal loanDebtGrossAmount) {}
