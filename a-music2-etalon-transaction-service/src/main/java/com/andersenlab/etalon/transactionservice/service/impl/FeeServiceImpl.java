package com.andersenlab.etalon.transactionservice.service.impl;

import com.andersenlab.etalon.transactionservice.dto.common.FeeDto;
import com.andersenlab.etalon.transactionservice.service.FeeService;
import com.andersenlab.etalon.transactionservice.util.DecimalUtils;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class FeeServiceImpl implements FeeService {
  public static final BigDecimal SINGLE_FEE_THRESHOLD = BigDecimal.valueOf(100);
  public static final BigDecimal FEE_BELOW_THRESHOLD = BigDecimal.valueOf(1.00);
  public static final BigDecimal FEE_RATE_ABOVE_THRESHOLD = BigDecimal.valueOf(0.01);

  public static final BigDecimal FEE_BELOW_THRESHOLD_FOREIGN_BANK = BigDecimal.valueOf(2.00);
  public static final BigDecimal FEE_RATE_ABOVE_THRESHOLD_FOREIGN_BANK = BigDecimal.valueOf(0.02);

  @Override
  public FeeDto calculateFeeLocalBank(BigDecimal amount) {

    return amount.compareTo(SINGLE_FEE_THRESHOLD) <= 0
        ? FeeDto.builder().amount(FEE_BELOW_THRESHOLD).build()
        : FeeDto.builder()
            .amount(DecimalUtils.round(DecimalUtils.multiply(amount, FEE_RATE_ABOVE_THRESHOLD)))
            .rate(FEE_RATE_ABOVE_THRESHOLD)
            .build();
  }

  @Override
  public FeeDto calculateFeeForeignBank(BigDecimal amount) {
    return amount.compareTo(SINGLE_FEE_THRESHOLD) <= 0
        ? FeeDto.builder().amount(FEE_BELOW_THRESHOLD_FOREIGN_BANK).build()
        : FeeDto.builder()
            .amount(
                DecimalUtils.round(
                    DecimalUtils.multiply(amount, FEE_RATE_ABOVE_THRESHOLD_FOREIGN_BANK)))
            .rate(FEE_RATE_ABOVE_THRESHOLD_FOREIGN_BANK)
            .build();
  }

  public FeeDto calculateTransferFee(
      BigDecimal amount, boolean isForeignBank, boolean isDifferentUser) {
    if (!isDifferentUser) {
      return FeeDto.builder().amount(BigDecimal.ZERO).build();
    }
    return isForeignBank ? calculateFeeForeignBank(amount) : calculateFeeLocalBank(amount);
  }
}
