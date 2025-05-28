package com.andersenlab.etalon.infoservice.util;

public class Constants {
  public static final String ETALON_BIN_VISA = "424670";
  public static final String ETALON_BIN_MASTERCARD = "524670";
  public static final String ETALON_BANK_CODE = "234567";
  public static final String EMAIL_TO = "etalon_dev_new_email1@yopmail.com";
  public static final String EMAIL_FROM = "etalon_dev_email@yopmail.com";
  public static final String IMAGE_PATH = "/documents/images/Etalon_Bank.png";
  public static final String PDF_TYPE = "pdf";
  public static final String DEFAULT_FILE_NAME_FORMAT = "%s.%s";
  public static final String TRANSACTIONS_PERIOD_FORMAT = "%s - %s";
  public static final String ACCOUNT_NUMBER_PATTERN = "[A-Z]{2}\\d{26}";
  public static final String INVALID_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String CARD_NUMBER_PATTERN = "\\d{16}";
  public static final String INVALID_CARD_NUMBER_MESSAGE =
      "Card number must contain exactly 16 digits";
  public static final String AUTHENTICATED_USER_ID = "authenticated-user-id";
  public static final String CONFIRMATION_CODE_PATTERN = "\\d{6}";
  public static final String CONFIRMATION_CODE_IS_NULL = "Confirmation code cannot be null";
  public static final String WRONG_CONFIRMATION_CODE_MESSAGE = "Invalid confirmation code format";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String BLANK_ACCOUNT_NUMBER = "Account number is required";
  public static final String INVALID_AMOUNT = "Invalid transaction amount";
  public static final String TERMS_AND_POLICY_FILE_NAME = "terms-and-conditions.pdf";
}
