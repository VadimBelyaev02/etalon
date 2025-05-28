package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.common.TransferReceiptContext;
import com.andersenlab.etalon.infoservice.dto.request.TransactionStatementPdfRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import java.io.File;
import java.util.List;
import java.util.Locale;

public interface PdfService {
  File createTransactionPdf(TransactionStatementPdfRequestDto dto, Locale locale);

  File createTransactionsStatement(
      UserDataResponseDto userData,
      List<TransactionDetailedResponseDto> transactionDetailedDtoList,
      String period,
      String locale);

  File createTransactionReceipt(TransferReceiptContext dto);
}
