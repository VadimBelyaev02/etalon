package com.andersenlab.etalon.infoservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  public static final String BRANCH_NOT_FOUND_BY_ID = "Cannot find bank branch with id %s";
  public static final String BANK_NOT_FOUND_BY_BIN = "Cannot find foreign bank with bin %s";
  public static final String BANK_NOT_FOUND_BY_BANK_CODE =
      "Cannot find foreign bank with bank code %s";
  public static final String INFORMATION_NOT_PROVIDED =
      "Both account number and card number are null. At least one of them must be provided.";
  public static final String CURRENCY_NOT_FOUND = "Currency definition %s not found";
  public static final String PARSING_FAILURE_OF_RECEIVED_RATES = "Can't parse received rates";
  public static final String USER_WITH_THIS_PESEL_ALREADY_REGISTERED =
      "User with such PESEL already registered";
  public static final String USER_ALREADY_EXISTS = "User already exists";
  public static final String NOT_VALID_CONFIRMATION_CODE = "Confirmation code is not valid";
  public static final String CONFIRMATION_REQUEST_NOT_FOUND_BY_ID =
      "Confirmation request with id %s is not found";
  public static final String CONFIRMATION_CONFLICT = "Operation has already been processed.";
  public static final String NO_ANY_USER_AUTHORIZED_FOUND = "No any user authorized found";
  public static final String CONFIRMATION_IS_BLOCKED_DUE_INCORRECT_ATTEMPTS =
      "Too many incorrect attempts. Confirmation is blocked. Please wait 10 minutes before retrying.";
  public static final String CONFIRMATION_IS_BLOCKED =
      "Confirmation is blocked. Please wait 10 minutes before retrying.";
  public static final String REGISTRATION_IS_BLOCKED =
      "Registration is blocked. Please wait 10 minutes before retrying.";
  public static final String CONFIRMATION_NOT_FOUND_BY_TARGET_ID =
      "Confirmation with targetId %s is not found";
  private final HttpStatus httpStatus;

  public BusinessException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
