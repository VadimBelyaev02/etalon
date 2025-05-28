package com.andersenlab.etalon.depositservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
  public static final String AUTHENTICATED_USER_ID = "authenticated-user-id";
  public static final String APPROVED = "APPROVED";
  public static final String DECLINED = "DECLINED";
  public static final String ACCOUNT_NUMBER_PATTERN = "[A-Z]{2}\\d{26}";
  public static final String ORDER_BY_PATTERN = "(?i)^(asc|desc)$";
  public static final String BLANK_ACCOUNT_NUMBER = "Account number is required";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String INVALID_AMOUNT = "Invalid transaction amount";
  public static final String BLANK_SOURCE = "Source account number is required";
  public static final int DEFAULT_SCALE = 2;
  public static final String DEPOSIT_INTEREST_HISTORY_DATE_FORMAT = "dd MMMM yyyy";
}
