package com.andersenlab.etalon.exceptionhandler.exception;

import static com.andersenlab.etalon.exceptionhandler.util.Constants.TRACE_ID;

import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;
import lombok.Getter;
import org.slf4j.MDC;

@Getter
public abstract class GenericException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String traceId;
  private final String serviceIdentifier;

  protected GenericException(String message, ErrorCode errorCode, String serviceIdentifier) {
    super(message);
    this.serviceIdentifier = serviceIdentifier;
    this.traceId = MDC.get(TRACE_ID);
    this.errorCode = errorCode;
  }

  protected GenericException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.traceId = MDC.get(TRACE_ID);
    this.serviceIdentifier = "";
  }
}
