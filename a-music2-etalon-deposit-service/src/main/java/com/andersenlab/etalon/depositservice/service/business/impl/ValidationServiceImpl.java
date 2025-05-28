package com.andersenlab.etalon.depositservice.service.business.impl;

import com.andersenlab.etalon.depositservice.client.service.AccountService;
import com.andersenlab.etalon.depositservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.service.business.ValidationService;
import com.andersenlab.etalon.depositservice.util.Constants;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
  private final AccountService accountService;

  @Override
  public void verifyAccountBelongsToUserAndIsNotBlocked(
      String userId, String verifiableAccountNumber) {
    AccountDetailedResponseDto accountDetailedResponseDto =
        accountService.getDetailedAccountInfo(verifiableAccountNumber);

    if (!accountDetailedResponseDto.userId().equals(userId)) {
      throw new BusinessException(
          HttpStatus.NOT_FOUND, BusinessException.OPERATION_REJECTED_INVALID_ACCOUNT_NUMBER);
    }
    if (Boolean.TRUE.equals(accountDetailedResponseDto.isBlocked())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TRANSACTION_REJECTED_ACCOUNT_BLOCKED);
    }
  }

  @Override
  public void checkAccountForThisAmount(
      String verifiableAccountNumber, BigDecimal withdrawalAmount) {
    AccountDetailedResponseDto accountDetailedResponseDto =
        accountService.getDetailedAccountInfo(verifiableAccountNumber);

    if (accountDetailedResponseDto.balance().compareTo(withdrawalAmount) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TRANSACTION_REJECTED_INSUFFICIENT_BALANCE);
    }
  }

  @Override
  public void validateAmount(BigDecimal amount) {
    if (amount.scale() > Constants.DEFAULT_SCALE) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.INVALID_AMOUNT_SCALE);
    }

    if (amount.compareTo(BigDecimal.ONE) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.AMOUNT_MUST_BE_POSITIVE);
    }
  }

  @Override
  public void checkIsValueInInterval(
      BigDecimal minValue, BigDecimal maxValue, BigDecimal verifiableValue) {
    if (verifiableValue.compareTo(minValue) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.VALUE_LESS_MINIMUM_REQUIRED);
    }

    if (verifiableValue.compareTo(maxValue) > 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.VALUE_MORE_MAXIMUM_REQUIRED);
    }
  }

  @Override
  public void validateUserAccount(
      String userId, AccountDetailedResponseDto withdrawalAccount, String depositAccountNumber) {
    if (!withdrawalAccount.userId().equals(userId)) {
      throw new BusinessException(
          HttpStatus.NOT_FOUND, BusinessException.OPERATION_REJECTED_INVALID_ACCOUNT_NUMBER);
    }
    if (Boolean.TRUE.equals(withdrawalAccount.isBlocked())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.TRANSACTION_REJECTED_ACCOUNT_BLOCKED);
    }
    if (withdrawalAccount.iban().equals(depositAccountNumber)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.WITHDRAWAL_ACCOUNT_SAME_AS_REPLENISH);
    }
  }

  @Override
  public void validateDepositEntityStatusAndMinimalOpenAmount(
      DepositEntity depositEntity, BigDecimal replenishAmount) {
    if (depositEntity.getStatus().equals(DepositStatus.EXPIRED)) {
      throw new BusinessException(HttpStatus.NOT_FOUND, BusinessException.DEPOSIT_IS_EXPIRED);
    }
    if (depositEntity.getStatus().equals(DepositStatus.CLOSED)
        && (replenishAmount.compareTo(depositEntity.getProduct().getMinOpenAmount()) < 0)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_REPLENISH_REJECTED_NOT_ENOUGH_AMOUNT);
    }
  }

  @Override
  public void validateDepositReplenishAmounts(
      BigDecimal replenishAmount,
      BigDecimal depositBalance,
      BigDecimal maxDepositReplenishAmount,
      BigDecimal accountBalance) {
    BigDecimal balanceAndReplenishSum = depositBalance.add(replenishAmount);
    if (replenishAmount.scale() > Constants.DEFAULT_SCALE) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_INVALID_AMOUNT_SCALE);
    }
    if (accountBalance.compareTo(replenishAmount) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_REPLENISH_REJECTED_INSUFFICIENT_FUNDS);
    }

    if (balanceAndReplenishSum.compareTo(maxDepositReplenishAmount) > 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_REPLENISH_REJECTED_MAXIMUM_AMOUNT);
    }

    if (replenishAmount.compareTo(BigDecimal.ONE) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_REPLENISH_REJECTED_MINIMAL_AMOUNT);
    }
  }

  @Override
  public void validatePossibilityForWithdraw(DepositEntity depositEntity) {
    if (Boolean.FALSE.equals(depositEntity.getProduct().getIsEarlyWithdrawal())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          BusinessException.TRANSACTION_REJECTED_EARLY_WITHDRAWAL_NOT_SUPPORTED);
    }
  }

  @Override
  public void validateDepositWithdrawAmounts(
      BigDecimal withdrawAmount, BigDecimal depositBalance, BigDecimal maxDepositWithdrawAmount) {

    if (ObjectUtils.isEmpty(withdrawAmount)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.AMOUNT_FROM_REQUEST_NULL);
    }
    int balanceAndWithdrawComparisonResult =
        getBalanceAndWithdrawComparisonResult(withdrawAmount, depositBalance);
    if (balanceAndWithdrawComparisonResult != 0
        && maxDepositWithdrawAmount.compareTo(withdrawAmount) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_PARTIAL_WITHDRAWAL_INSUFFICIENT_FUNDS);
    }
  }

  @Override
  public void verifyNoDuplicateAccounts(
      DepositEntity depositEntity, DepositUpdateRequestDto depositUpdateRequestDto) {
    if (depositEntity
            .getInterestAccountNumber()
            .equals(depositUpdateRequestDto.interestAccountNumber())
        || depositEntity
            .getFinalTransferAccountNumber()
            .equals(depositUpdateRequestDto.finalTransferAccountNumber())) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_UPDATE_REJECTED_SAME_ACCOUNTS);
    }
  }

  private static int getBalanceAndWithdrawComparisonResult(
      BigDecimal withdrawAmount, BigDecimal depositBalance) {
    int balanceAndWithdrawComparisonResult = depositBalance.compareTo(withdrawAmount);
    if (withdrawAmount.scale() > 2) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_WITHDRAWAL_INVALID_AMOUNT_SCALE);
    }
    if (withdrawAmount.compareTo(BigDecimal.ONE) < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_WITHDRAWAL_MIN_AMOUNT);
    }
    if (balanceAndWithdrawComparisonResult < 0) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.DEPOSIT_FULL_WITHDRAWAL_INSUFFICIENT_FUNDS);
    }
    return balanceAndWithdrawComparisonResult;
  }
}
