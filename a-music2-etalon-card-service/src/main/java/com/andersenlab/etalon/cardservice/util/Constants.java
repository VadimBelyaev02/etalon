package com.andersenlab.etalon.cardservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  public static final String CARD_NUMBER_PATTERN = "\\d{16}";
  public static final String WRONG_CARD_NUMBER_MESSAGE =
      "Card number must contain exactly 16 digits";
  public static final int DEFAULT_SCALE = 2;
  public static final String JSON_MASK_FULL_NAME_PATTERN = "(\\b\\w+\\b)\\s(\\b\\w)\\w*";
  public static final String JSON_MASK_FULL_NAME_REPLACE_PATTERN = "$1 $2.";
  public static final String CREATED_AT = "createAt";
  public static final String ACCOUNT_NUMBER_PATTERN = "[A-Z]{2}\\d{26}";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
}
