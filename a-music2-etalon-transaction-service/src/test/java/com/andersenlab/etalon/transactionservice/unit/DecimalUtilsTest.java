package com.andersenlab.etalon.transactionservice.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.util.DecimalUtils;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DecimalUtilsTest {

  @Test
  void testMultiply_success() {
    BigDecimal a = new BigDecimal("10.123");
    BigDecimal b = new BigDecimal("2.5");

    BigDecimal result = DecimalUtils.multiply(a, b);
    Assertions.assertEquals(new BigDecimal("25.3075"), result);
  }

  @Test
  void testMultiply_nullHandling() {
    Assertions.assertThrows(
        BusinessException.class, () -> DecimalUtils.multiply(null, BigDecimal.TEN));
  }

  @Test
  void testDivide_success() {
    BigDecimal a = new BigDecimal("10");
    BigDecimal b = new BigDecimal("2");

    BigDecimal result = DecimalUtils.divide(a, b);
    Assertions.assertEquals(new BigDecimal("5.0000"), result);
  }

  @Test
  void testDivide_byZero() {
    Exception exception =
        assertThrows(
            BusinessException.class, () -> DecimalUtils.divide(BigDecimal.TEN, BigDecimal.ZERO));

    String expectedMessage = "Cannot divide by null or zero";
    String actualMessage = exception.getMessage();
    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testRound_success() {
    BigDecimal value = new BigDecimal("123.456");

    BigDecimal result = DecimalUtils.round(value);
    Assertions.assertEquals(new BigDecimal("123.46"), result);
  }

  @Test
  void testRound_nullHandling() {
    Assertions.assertThrows(BusinessException.class, () -> DecimalUtils.round(null));
  }
}
