package com.andersenlab.etalon.transactionservice.service;

import com.andersenlab.etalon.transactionservice.dto.common.FeeDto;
import java.math.BigDecimal;

public interface FeeService {
  FeeDto calculateFeeLocalBank(BigDecimal amount);

  FeeDto calculateFeeForeignBank(BigDecimal amount);

  FeeDto calculateTransferFee(BigDecimal amount, boolean isForeignBank, boolean isDifferentUser);
}
