package com.andersenlab.etalon.exceptionhandler.exception;

import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;

public class ConflictException extends GenericException {

  public ConflictException(String message) {
    super(message, ErrorCode.CONFLICT);
  }
}
