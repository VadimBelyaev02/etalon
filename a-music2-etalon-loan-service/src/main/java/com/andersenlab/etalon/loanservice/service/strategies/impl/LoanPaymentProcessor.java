package com.andersenlab.etalon.loanservice.service.strategies.impl;

import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.service.strategies.LoanPaymentStrategy;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class LoanPaymentProcessor {
  private final Map<LoanStatus, LoanPaymentStrategy> strategies;

  @Autowired
  public LoanPaymentProcessor(List<LoanPaymentStrategy> strategyList) {
    this.strategies =
        strategyList.stream()
            .collect(
                Collectors.toMap(LoanPaymentStrategy::getSupportedStatus, Function.identity()));
  }

  public void processLoanPayment(LoanEntity loanEntity, LoanPaymentRequestDto paymentDto) {

    LoanPaymentStrategy strategy = strategies.get(loanEntity.getStatus());
    if (Objects.isNull(strategy)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          String.format(BusinessException.UNSUPPORTED_LOAN_STATUS, loanEntity.getStatus()));
    }
    strategy.processPayment(loanEntity, paymentDto);
  }
}
