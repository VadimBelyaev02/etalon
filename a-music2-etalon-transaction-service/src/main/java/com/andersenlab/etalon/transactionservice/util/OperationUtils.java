package com.andersenlab.etalon.transactionservice.util;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OperationUtils {

  public static final String BANK_PAYMENT_COMMISSION = "0.3";
  public static final BigDecimal PERCENTAGE_OF_BANK_PAYMENT_COMMISSION =
      new BigDecimal(BANK_PAYMENT_COMMISSION);

  public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

  public static BigDecimal calculatePaymentCommission(BigDecimal paymentAmount) {
    return DecimalUtils.round(
        DecimalUtils.divide(
            DecimalUtils.multiply(paymentAmount, PERCENTAGE_OF_BANK_PAYMENT_COMMISSION),
            ONE_HUNDRED));
  }
}
