package com.andersenlab.etalon.infoservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BankIdentifiersUtils {
  public static String getBinFromCardNumber(String cardNumber) {
    return cardNumber.substring(0, 6);
  }

  public static String getBankCodeFromIban(String iban) {
    return iban.substring(4, 10);
  }
}
