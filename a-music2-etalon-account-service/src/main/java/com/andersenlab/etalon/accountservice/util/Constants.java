package com.andersenlab.etalon.accountservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
  public static final String PHONE_NUMBER_PATTERN = "48\\d{9}$";
  public static final String ACCOUNT_NUMBER_PATTERN = "[A-Z]{2}\\d{26}";
  public static final String CARD_NUMBER_PATTERN = "\\d{16}";
  public static final String WRONG_PHONE_NUMBER_MESSAGE = "Invalid phone format";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String WRONG_CARD_NUMBER_MESSAGE =
      "Card number must contain exactly 16 digits";
  public static final String JSON_MASK_FULL_NAME_PATTERN = "(\\b\\w+\\b)\\s(\\b\\w)\\w*";
  public static final String JSON_MASK_FULL_NAME_REPLACE_PATTERN = "$1 $2.";
}
