package com.andersenlab.etalon.loanservice.util;

import java.math.BigDecimal;

public class Constants {
  public static final Integer MONTHS_IN_YEAR = 12;
  public static final String ACCOUNT_NUMBER_PATTERN = "[A-Z]{2}\\d{26}";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String BLANK_ACCOUNT_NUMBER = "Account number is required";
  public static final String INVALID_AMOUNT = "Invalid transaction amount";
  public static final String BLANK_SOURCE = "Source account number is required";
  public static final String BLANK_BENEFICIARY = "Beneficiary account number is required";
  public static final BigDecimal DAILY_PENALTY_RATE = BigDecimal.valueOf(0.0001);
  public static final int INTERMEDIATE_SCALE = 4;
  public static final int FINAL_SCALE = 2;
}
