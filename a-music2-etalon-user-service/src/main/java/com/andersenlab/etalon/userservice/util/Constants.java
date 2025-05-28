package com.andersenlab.etalon.userservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {
  public static final String EMAIL_PATTERN =
      "^(?!.*[._-]{2})[a-zA-Z0-9](?:[a-zA-Z0-9._-]*[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,6}$";
  public static final String STARTS_OR_ENDS_WITH_SPACE_OR_HYPHEN_ERROR =
      "%s cannot start or end with a space or hyphen.";
  public static final String INVALID_CHARACTER_ERROR =
      "%s may only contain Latin letters, digits, spaces, special characters (only - \" , ' ( ) . « » ).";
  public static final String WRONG_EMAIL_MESSAGE =
      "Email must be in the 'username@domain.com format";
  public static final String PESEL_MUST_NOT_BE_BLANK = "Pesel must not be blank";
  public static final String PESEL_MUST_CONTAIN_11_DIGITS = "Pesel must contain 11 digits";
  public static final String PESEL_MUST_CONTAIN_ONLY_DIGITS = "Pesel must contain only digits";
  public static final String BANKS_PRIVACY_POLICY_MUST_BE_ACCEPTED =
      "Bank's Privacy Policy must be accepted";
  public static final String REGISTRATION_WAS_COMPLETED_SUCCESSFULLY =
      "Registration was completed successfully";
}
