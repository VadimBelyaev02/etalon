package com.andersenlab.etalon.loanservice.service;

import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.dto.loan.request.DelinquentLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import java.math.BigDecimal;

public interface ValidationService {

  void validateLoanOrderRequestDto(LoanOrderRequestDto dto);

  void validateGuarantors(LoanOrderRequestDto dto);

  void validatePaymentAccount(String userId, String paymentAccountNumber);

  void validatePaymentAmount(BigDecimal paymentAmount, BigDecimal expectedAmount);

  void validateSufficientFunds(BigDecimal paymentAmount, BigDecimal accountBalance);

  void validatePaymentAmountsForDelinquent(
      DelinquentLoanPaymentRequestDto delinquentDto,
      LoanCalculationResult calculationResult,
      BigDecimal expectedPenalty);
}
