package com.andersenlab.etalon.exceptionhandler.exception;

import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FeignClientException extends GenericException {
  private final HttpStatus status;

  public FeignClientException(
      String message, ErrorCode errorCode, String serviceIdentifier, HttpStatus status) {
    super(message, errorCode, serviceIdentifier);
    this.status = status;
  }
}
