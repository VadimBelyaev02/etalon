package com.andersenlab.etalon.transactionservice.dto.common.response;

import com.andersenlab.etalon.transactionservice.util.Constants;
import java.time.LocalDateTime;
import org.slf4j.MDC;

public record ErrorResponse(LocalDateTime timestamp, String traceId, String message) {
  public ErrorResponse(String message) {
    this(LocalDateTime.now(), MDC.get(Constants.TRACE_ID), message);
  }
}
