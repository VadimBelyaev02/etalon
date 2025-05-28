package com.andersenlab.etalon.depositservice.service.business;

import com.andersenlab.etalon.depositservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import java.math.BigDecimal;

public interface ValidationService {
  void verifyAccountBelongsToUserAndIsNotBlocked(String userId, String account);

  void checkAccountForThisAmount(String account, BigDecimal amount);

  void validateAmount(BigDecimal amount);

  void checkIsValueInInterval(BigDecimal minValue, BigDecimal maxValue, BigDecimal verifiableValue);

  void validateUserAccount(
      String userId, AccountDetailedResponseDto destinationAccount, String depositAccountNumber);

  void validateDepositEntityStatusAndMinimalOpenAmount(
      DepositEntity depositEntity, BigDecimal replenishAmount);

  void validateDepositReplenishAmounts(
      BigDecimal replenishAmount,
      BigDecimal depositBalance,
      BigDecimal maxDepositReplenishAmount,
      BigDecimal accountBalance);

  void validatePossibilityForWithdraw(DepositEntity entity);

  void validateDepositWithdrawAmounts(
      BigDecimal withdrawAmount, BigDecimal depositBalance, BigDecimal maxDepositWithdrawAmount);

  void verifyNoDuplicateAccounts(
      DepositEntity depositEntity, DepositUpdateRequestDto depositUpdateRequestDto);
}
