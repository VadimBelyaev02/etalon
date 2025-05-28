package com.andersenlab.etalon.loanservice.service.strategies.impl;

import com.andersenlab.etalon.loanservice.client.AccountServiceClient;
import com.andersenlab.etalon.loanservice.client.TransactionServiceClient;
import com.andersenlab.etalon.loanservice.config.TimeProvider;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountBalanceResponseDto;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.dto.loan.request.ActiveLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.loanservice.entity.LoanEntity;
import com.andersenlab.etalon.loanservice.entity.LoanPaymentEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.repository.LoanPaymentRepository;
import com.andersenlab.etalon.loanservice.service.ValidationService;
import com.andersenlab.etalon.loanservice.service.strategies.LoanPaymentStrategy;
import com.andersenlab.etalon.loanservice.util.LoanUtils;
import com.andersenlab.etalon.loanservice.util.enums.Details;
import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActiveLoanPaymentStrategy implements LoanPaymentStrategy {

  private static final String REPLENISH_LOAN_TRANSACTION_NAME = "Payment for %s";
  private final LoanPaymentRepository loanPaymentRepository;
  private final AccountServiceClient accountServiceClient;
  private final TimeProvider timeProvider;
  private final TransactionServiceClient transactionServiceClient;
  private final ValidationService validationService;

  @Override
  public LoanStatus getSupportedStatus() {
    return LoanStatus.ACTIVE;
  }

  @Override
  public void processPayment(LoanEntity loanEntity, LoanPaymentRequestDto paymentRequestDto) {
    if (!(paymentRequestDto instanceof ActiveLoanPaymentRequestDto activeDto)) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.INVALID_REQUEST_DATA);
    }

    String paymentAccountNumber = activeDto.paymentAccountNumber();
    ZonedDateTime currentDateTime = timeProvider.getCurrentZonedDateTime();
    String userId = loanEntity.getUserId();

    validationService.validatePaymentAccount(userId, paymentAccountNumber);

    BigDecimal paymentAccountBalance = getAccountBalance(paymentAccountNumber);
    BigDecimal loanAccountBalance = getLoanAccountBalance(loanEntity.getAccountNumber());

    LoanCalculationResult calculationResult =
        LoanUtils.calculateLoanPayments(
            LoanUtils.collectLoanCalculationInitialData(loanEntity),
            loanAccountBalance,
            currentDateTime);

    BigDecimal expectedTotalPaymentAmount =
        calculationResult.loanDebtGrossAmount().setScale(2, RoundingMode.CEILING);

    validationService.validateSufficientFunds(
        activeDto.totalPaymentAmount(), paymentAccountBalance);

    validationService.validatePaymentAmount(
        activeDto.totalPaymentAmount(), expectedTotalPaymentAmount);

    updateLoanEntity(loanEntity, calculationResult);

    saveLoanPaymentTransaction(loanEntity, calculationResult, BigDecimal.ZERO);

    createTransaction(loanEntity, activeDto, calculationResult, BigDecimal.ZERO);
  }

  private BigDecimal getLoanAccountBalance(String loanAccountNumber) {
    AccountBalanceResponseDto response =
        accountServiceClient.getAccountBalanceByAccountNumber(loanAccountNumber);
    return response.accountBalance();
  }

  private BigDecimal getAccountBalance(String accountNumber) {
    AccountBalanceResponseDto response =
        accountServiceClient.getAccountBalanceByAccountNumber(accountNumber);
    return response.accountBalance();
  }

  private void updateLoanEntity(LoanEntity loanEntity, LoanCalculationResult calculationResult) {
    BigDecimal amountToCloseLoan =
        loanEntity.getAmount().subtract(getLoanAccountBalance(loanEntity.getAccountNumber()));
    if (amountToCloseLoan.compareTo(calculationResult.loanDebtNetAmount()) <= 0) {
      loanEntity.setStatus(LoanStatus.CLOSED);
    } else {
      loanEntity.setNextPaymentDate(loanEntity.getNextPaymentDate().plusMonths(1));
      if (loanEntity.getStatus() == LoanStatus.DELINQUENT) {
        loanEntity.setStatus(LoanStatus.ACTIVE);
      }
    }
    loanEntity.setUpdateAt(timeProvider.getCurrentZonedDateTime());
  }

  private void saveLoanPaymentTransaction(
      LoanEntity loanEntity, LoanCalculationResult calculationResult, BigDecimal penaltyAmount) {

    LoanPaymentEntity loanPayment =
        LoanPaymentEntity.builder()
            .loan(loanEntity)
            .principalPaymentAmount(calculationResult.loanDebtNetAmount())
            .accruedInterest(calculationResult.loanInterestAmount())
            .accruedCommission(calculationResult.monthlyCommissionAmount())
            .penalty(penaltyAmount)
            .totalPaymentAmount(calculationResult.loanDebtGrossAmount().add(penaltyAmount))
            .build();

    loanPaymentRepository.save(loanPayment);
  }

  private void createTransaction(
      LoanEntity loanEntity,
      ActiveLoanPaymentRequestDto paymentDto,
      LoanCalculationResult calculationResult,
      BigDecimal penaltyAmount) {

    TransactionCreateRequestDto transactionRequest =
        TransactionCreateRequestDto.builder()
            .transactionName(
                String.format(REPLENISH_LOAN_TRANSACTION_NAME, loanEntity.getProduct().getName()))
            .accountNumberReplenished(loanEntity.getAccountNumber())
            .accountNumberWithdrawn(paymentDto.paymentAccountNumber())
            .amount(calculationResult.loanDebtNetAmount())
            .loanInterestAmount(calculationResult.loanInterestAmount())
            .isFeeProvided(
                calculationResult.monthlyCommissionAmount().compareTo(BigDecimal.ZERO) > 0)
            .feeAmount(calculationResult.monthlyCommissionAmount())
            .loanPenaltyAmount(penaltyAmount)
            .details(Details.LOAN)
            .build();

    transactionServiceClient.createTransaction(transactionRequest);
  }
}
