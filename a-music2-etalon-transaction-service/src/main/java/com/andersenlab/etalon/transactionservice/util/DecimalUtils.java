package com.andersenlab.etalon.transactionservice.util;

import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;

@UtilityClass
public class DecimalUtils {

  public static final int DEFAULT_CALCULATIONS_SCALE = 4;
  public static final int DEFAULT_OUTPUT_SCALE = 2;
  private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

  public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
    if (ObjectUtils.anyNull(a, b)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.MATHEMATICAL_OPERATIONS_WITH_NULL);
    }
    return a.multiply(b).setScale(DEFAULT_CALCULATIONS_SCALE, DEFAULT_ROUNDING_MODE);
  }

  public static BigDecimal divide(BigDecimal a, BigDecimal b) {
    if (ObjectUtils.anyNull(a, b) || b.equals(BigDecimal.ZERO)) {
      throw new BusinessException(HttpStatus.BAD_REQUEST, BusinessException.DIVIDE_BY_NULL_OR_ZERO);
    }
    return a.divide(b, DEFAULT_CALCULATIONS_SCALE, DEFAULT_ROUNDING_MODE);
  }

  public static BigDecimal round(BigDecimal value) {
    if (ObjectUtils.isEmpty(value)) {
      throw new BusinessException(
          HttpStatus.BAD_REQUEST, BusinessException.MATHEMATICAL_OPERATIONS_WITH_NULL);
    }
    return value.setScale(DEFAULT_OUTPUT_SCALE, DEFAULT_ROUNDING_MODE);
  }
}
