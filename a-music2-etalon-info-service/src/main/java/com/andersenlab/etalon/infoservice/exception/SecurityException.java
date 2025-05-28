package com.andersenlab.etalon.infoservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SecurityException extends RuntimeException {

  public static final String LOGIN_ERROR = "Invalid email or password";
  public static final String TOKEN_REFRESHED_ERROR = "Failed to refresh token";
  public static final String LOGOUT_ERROR = "Failed to logout";
  public static final String CONFIRMATION_CODE_IS_NOT_VALID = "Invalid code. Please try again";
  public static final String CONFIRMATION_CODE_IS_BLOCKED =
      "This verification code has blocked. Please request a new code";
  public static final String INVALID_CURRENT_PASSWORD = "Current password is not matches";
  private final HttpStatus httpStatus;

  public SecurityException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
