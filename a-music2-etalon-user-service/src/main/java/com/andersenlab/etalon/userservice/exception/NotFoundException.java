package com.andersenlab.etalon.userservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {
  public static final String USER_NOT_FOUND = "User with such id (%s) is not found";
  public static final String CLIENT_WITH_PROVIDED_PESEL_NOT_FOUND =
      "Client with provided pesel not found";
  public static final String REGISTRATION_WITH_PROVIDED_TARGET_ID_NOT_FOUND =
      "Registration with provided target id not found, targetId: ";
  public static final String REGISTRATION_WITH_PROVIDED_REGISTRATION_ID_NOT_FOUND =
      "Registration with provided registration id not found, registrationId: ";
  public static final String DEFAULT_ROLE_NOT_FOUND = "Default role not found";

  public static final String AN_EMAIL_MODIFICATION_RECORD_NOT_FOUND =
      "User email modification id(%d) does not exist";
  private final HttpStatus httpStatus;

  public NotFoundException(final String message) {
    super(message);
    this.httpStatus = HttpStatus.NOT_FOUND;
  }
}
