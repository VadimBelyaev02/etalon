package com.andersenlab.etalon.loanservice.service.strategies;

import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;

public interface LoanPaymentStrategy {

  LoanStatus getSupportedStatus();

  void processPayment(LoanEntity loanEntity, LoanPaymentRequestDto paymentRequestDto);
}
