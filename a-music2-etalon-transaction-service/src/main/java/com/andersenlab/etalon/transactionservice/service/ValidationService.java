package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.account.response.AccountDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.StatusMessageResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import java.math.BigDecimal;
import java.util.List;

public interface ValidationService {
  void validateOwnership(
      List<String> validatingAccounts, List<String> validAccounts, BusinessException exception);

  void validateAmountMoreThanOne(BigDecimal amount);

  void validateAmountMoreThanZero(BigDecimal amount);

  StatusMessageResponseDto validateTemplateName(String templateName, String userId);

  void validatePaymentStatus(PaymentStatus status);

  void validateTransferStatus(TransferStatus status);

  void validateAccount(AccountDetailedResponseDto accountDetailedResponseDto);

  void validateProductType(Long productId);

  void validateAccountBalance(BigDecimal amount, String accountNumber);

  void validateCardLimits(String accountNumber, BigDecimal amount);
}
