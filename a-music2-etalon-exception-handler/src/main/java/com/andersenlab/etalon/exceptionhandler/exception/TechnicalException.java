package com.andersenlab.etalon.exceptionhandler.exception;

import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;
import lombok.Getter;

@Getter
public class TechnicalException extends GenericException {

  public TechnicalException(final String message) {
    super(message, ErrorCode.TECHNICAL_EXCEPTION);
  }
}
