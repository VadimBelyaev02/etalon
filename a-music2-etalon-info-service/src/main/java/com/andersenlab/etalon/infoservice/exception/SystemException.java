package com.andersenlab.etalon.infoservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SystemException extends RuntimeException {

  public static final String INTERNAL_SERVER_ERROR = "An error occurred on server side";

  private final HttpStatus httpStatus;

  public SystemException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
