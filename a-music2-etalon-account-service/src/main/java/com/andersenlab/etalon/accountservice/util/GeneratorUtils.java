package com.andersenlab.etalon.accountservice.util;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class GeneratorUtils {

  private static final Long INITIAL_ACCOUNT_SERIAL_NUMBER = 1L;
  private static final long MODULUS = 97;

  public static String generateAccountNumber(Long serialNumber) {
    Long nextSerialNumber =
        Objects.nonNull(serialNumber) ? ++serialNumber : INITIAL_ACCOUNT_SERIAL_NUMBER;
    return StringUtils.leftPad(nextSerialNumber.toString(), 16, "0");
  }

  public static String getChecksum(String number) {
    String addedValue = "0";
    int modulusResult = getCheckNumberMOD97(number);
    int charValue = (98 - modulusResult);
    String checkDigit = Integer.toString(charValue);
    return (charValue > 9 ? checkDigit : addedValue + checkDigit);
  }

  private static int getCheckNumberMOD97(String number) {
    long total = 0;

    for (int i = 0; i < number.length(); i++) {
      int charValue = Character.getNumericValue(number.charAt(i));
      total = (charValue > 9 ? total * 100 : total * 10) + charValue;
      if (total > 999999999) {
        total = total % MODULUS;
      }
    }
    return (int) (total % MODULUS);
  }
}
