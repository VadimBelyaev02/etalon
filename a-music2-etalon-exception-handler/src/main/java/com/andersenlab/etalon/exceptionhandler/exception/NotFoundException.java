package com.andersenlab.etalon.exceptionhandler.exception;

import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;

public class NotFoundException extends GenericException {

  public NotFoundException(String message) {
    super(message, ErrorCode.NOT_FOUND);
  }
}
