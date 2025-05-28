package com.andersenlab.etalon.loanservice.dto.loan.request;

import java.math.BigDecimal;

public interface LoanPaymentRequestDto {
  String paymentAccountNumber();

  BigDecimal totalPaymentAmount();
}
