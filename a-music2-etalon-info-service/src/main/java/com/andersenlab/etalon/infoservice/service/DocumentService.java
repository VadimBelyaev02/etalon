package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.FileDto;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;

public interface DocumentService {
  FileDto createTransactionConfirmation(String userId, Long transactionId, String locale);

  FileDto createTransactionsStatement(
      String userId, TransactionFilter filter, String statementType, String locale);

  FileDto createTransferReceipt(Long transferId, String userId);

  FileDto downloadTermsAndPolicy();
}
