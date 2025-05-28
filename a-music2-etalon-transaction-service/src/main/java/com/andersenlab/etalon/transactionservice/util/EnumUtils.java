package com.andersenlab.etalon.transactionservice.util;

import com.andersenlab.etalon.transactionservice.exception.EnumConversionException;
import java.util.Arrays;

public class EnumUtils {
  public static <E extends Enum<E>> E getEnumValue(String value, Class<E> enumClass) {
    if (value == null) {
      return null;
    }
    try {
      return Enum.valueOf(enumClass, value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new EnumConversionException(
          "Invalid value. Possible values for "
              + enumClass.getSimpleName()
              + ": "
              + Arrays.toString(enumClass.getEnumConstants()));
    }
  }
}
