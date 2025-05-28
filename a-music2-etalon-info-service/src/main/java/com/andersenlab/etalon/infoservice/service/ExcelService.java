package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.dto.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import java.io.File;
import java.util.List;

public interface ExcelService {
  File createTransactionsStatement(
      UserDataResponseDto userData,
      List<TransactionDetailedResponseDto> transactionDetailedDtoList,
      String period,
      String locale);
}
