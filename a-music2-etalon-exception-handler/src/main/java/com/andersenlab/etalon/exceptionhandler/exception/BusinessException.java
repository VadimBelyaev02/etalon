package com.andersenlab.etalon.exceptionhandler.exception;

import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends GenericException {

  public BusinessException(String message) {
    super(message, ErrorCode.BUSINESS_EXCEPTION);
  }
}
