package com.andersenlab.etalon.loanservice.service.impl;

import com.andersenlab.etalon.loanservice.client.AccountServiceClient;
import com.andersenlab.etalon.loanservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.calculation.LoanCalculationResult;
import com.andersenlab.etalon.loanservice.dto.loan.request.DelinquentLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.GuarantorRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.entity.GuarantorEntity;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.exception.ValidationException;
import com.andersenlab.etalon.loanservice.repository.GuarantorRepository;
import com.andersenlab.etalon.loanservice.repository.LoanProductRepository;
import com.andersenlab.etalon.loanservice.service.ValidationService;
import com.andersenlab.etalon.loanservice.util.LoanOrderUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

  private final LoanProductRepository loanProductRepository;
  private final GuarantorRepository guarantorRepository;
  private final AccountServiceClient accountServiceClient;

  @Override
  public void validateLoanOrderRequestDto(LoanOrderRequestDto dto) {

    LoanProductEntity loanProduct =
        loanProductRepository
            .findById(dto.productId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        HttpStatus.NOT_FOUND,
                        String.format(
                            BusinessException.LOAN_PRODUCT_NOT_FOUND_BY_ID, dto.productId())));

    if (dto.amount().compareTo(loanProduct.getMinAmount()) < 0
        || dto.amount().compareTo(loanProduct.getMaxAmount()) > 0) {
      throw new ValidationException(HttpStatus.BAD_REQUEST, ValidationException.INCORRECT_AMOUNT);
    }

    if (!dto.duration().equals(loanProduct.getDuration())) {
      throw new ValidationException(HttpStatus.BAD_REQUEST, ValidationException.INCORRECT_DURATION);
    }

    if (dto.averageMonthlySalary().compareTo(dto.averageMonthlyExpenses()) < 0) {
      throw new ValidationException(
          HttpStatus.BAD_REQUEST, ValidationException.SALARY_LESS_THAN_EXPENSES);
    }

    if (Objects.nonNull(loanProduct.getRequiredGuarantors())) {
      if (Objects.isNull(dto.guarantors())
          || dto.guarantors().size() != loanProduct.getRequiredGuarantors()) {
        throw new ValidationException(
            HttpStatus.BAD_REQUEST, ValidationException.INVALID_GUARANTOR_COUNT);
      }

      validateGuarantors(dto);

    } else if (!CollectionUtils.isEmpty(dto.guarantors())) {
      throw new ValidationException(
          HttpStatus.BAD_REQUEST, ValidationException.INVALID_GUARANTOR_COUNT);
    }
  }

  public void validateGuarantors(LoanOrderRequestDto dto) {
    Set<GuarantorRequestDto> guarantorRequestDtos = dto.guarantors();

    Set<GuarantorEntity> existingGuarantors =
        guarantorRepository.findAllByPeselIn(
            LoanOrderUtils.collectPeselsFromGuarantors(guarantorRequestDtos));

    existingGuarantors.forEach(
        existingGuarantor ->
            guarantorRequestDtos.stream()
                .filter(newGuarantor -> existingGuarantor.getPesel().equals(newGuarantor.pesel()))
                .findFirst()
                .ifPresent(
                    newGuarantor -> {
                      if (!existingGuarantor
                              .getFirstName()
                              .equalsIgnoreCase(newGuarantor.firstName())
                          || !existingGuarantor
                              .getLastName()
                              .equalsIgnoreCase(newGuarantor.lastName())) {
                        throw new ValidationException(
                            HttpStatus.BAD_REQUEST, ValidationException.GUARANTOR_ALREADY_EXIST);
                      }
                    }));
  }

  @Override
  public void validatePaymentAccount(String userId, String paymentAccountNumber) {
    AccountDetailedResponseDto accountDetails =
        accountServiceClient.getDetailedAccountInfo(paymentAccountNumber);

    if (!userId.equals(accountDetails.userId())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.TRANSACTION_REJECTED_DUE_TO_INVALID_ACCOUNT_NUMBER);
    }

    if (Boolean.TRUE.equals(accountDetails.isBlocked())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TRANSACTION_REJECTED_DUE_TO_ACCOUNT_BLOCKING);
    }
  }

  @Override
  public void validatePaymentAmount(BigDecimal paymentAmount, BigDecimal expectedAmount) {
    int comparison = paymentAmount.compareTo(expectedAmount);
    if (comparison > 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.LOAN_PAYMENT_REJECTED_DUE_TO_EXCESSIVE_AMOUNT);
    } else if (comparison < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.LOAN_PAYMENT_REJECTED_DUE_TO_INSUFFICIENT_AMOUNT);
    }
  }

  @Override
  public void validateSufficientFunds(BigDecimal paymentAmount, BigDecimal accountBalance) {
    if (paymentAmount.compareTo(accountBalance) > 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT);
    }
  }

  @Override
  public void validatePaymentAmountsForDelinquent(
      DelinquentLoanPaymentRequestDto delinquentDto,
      LoanCalculationResult calculationResult,
      BigDecimal expectedPenalty) {

    validateAmount(delinquentDto.principalPaymentAmount(), calculationResult.loanDebtNetAmount());
    validateAmount(delinquentDto.accruedInterest(), calculationResult.loanInterestAmount());
    validateAmount(delinquentDto.accruedCommission(), calculationResult.monthlyCommissionAmount());
    validateAmount(delinquentDto.penalty(), expectedPenalty);

    BigDecimal expectedTotalPaymentAmount =
        calculationResult
            .loanDebtGrossAmount()
            .add(expectedPenalty)
            .setScale(2, RoundingMode.CEILING);

    validatePaymentAmount(delinquentDto.totalPaymentAmount(), expectedTotalPaymentAmount);
  }

  private void validateAmount(BigDecimal expected, BigDecimal actual) {
    if (expected.compareTo(actual) != 0) {
      throw new BusinessException(
          HttpStatus.CONFLICT, BusinessException.PAYMENT_AMOUNT_HAS_CHANGED_TRY_AGAIN);
    }
  }
}
