package com.andersenlab.etalon.transactionservice.unit;

import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.util.OperationUtils;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OperationUtilsTest {

  @ParameterizedTest
  @CsvSource({"3.00,1000", "0.00,1.00", "1.00,333.25", "0.01,3"})
  void calculatePaymentCommission_thenSuccess(String expectedValue, String inputValue) {
    // given
    BigDecimal expectedResult = new BigDecimal(expectedValue);
    BigDecimal paymentAmount = new BigDecimal(inputValue);

    // when
    BigDecimal actualResult = OperationUtils.calculatePaymentCommission(paymentAmount);

    // then
    Assertions.assertEquals(expectedResult, actualResult);
  }

  @Test
  void testCalculatePaymentCommission_success() {
    BigDecimal paymentAmount = new BigDecimal("1000"); // Example payment of 1000

    BigDecimal result = OperationUtils.calculatePaymentCommission(paymentAmount);
    Assertions.assertEquals(new BigDecimal("3.00"), result);
  }

  @Test
  void testCalculatePaymentCommission_nullPayment() {
    Assertions.assertThrows(
        BusinessException.class, () -> OperationUtils.calculatePaymentCommission(null));
  }

  @Test
  void testCalculatePaymentCommission_largePayment() {
    BigDecimal paymentAmount = new BigDecimal("100000");

    BigDecimal result = OperationUtils.calculatePaymentCommission(paymentAmount);
    Assertions.assertEquals(new BigDecimal("300.00"), result);
  }
}
