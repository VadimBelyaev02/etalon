package com.andersenlab.etalon.userservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  public static final String EMAIL_IS_NOT_AVAILABLE = "Email is not available to register";
  public static final String PESEL_IS_NOT_AVAILABLE = "PESEL is already registered";
  public static final String PHONE_NUMBER_INVALID = "Phone number is invalid";
  public static final String PHONE_NUMBER_IS_NOT_AVAILABLE = "Phone number is already registered";
  public static final String USER_WITH_PROVIDED_COGNITO_ID_ALREADY_REGISTERED =
      "User with provided cognitoId already registered";
  public static final String EMAIL_IS_ALREADY_REGISTERED = "Email is already registered";
  public static final String EMAIL_CAN_NOT_MATCH_WITH_CURRENT_ONE =
      "The new email cannot be the same as the current email";
  public static final String USER_WITH_PROVIDED_PESEL_ALREADY_REGISTERED =
      "User with provided pesel already registered";
  public static final String USER_WITH_PROVIDED_EMAIL_ALREADY_REGISTERED =
      "User with provided email already registered";
  public static final String REGISTRATION_IS_NOT_IN_PENDING_CONFIRMATION =
      "Registration is not in PENDING_CONFIRMATION " + "state";
  public static final String REGISTRATION_IS_NOT_IN_CONFIRMED_STATUS =
      "Registration is not in CONFIRMED state";
  public static final String PROVIDED_CODE_IS_NOT_MATCH_REQUIRED =
      "Provided code does not match required";
  private final HttpStatus httpStatus;

  public BusinessException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
