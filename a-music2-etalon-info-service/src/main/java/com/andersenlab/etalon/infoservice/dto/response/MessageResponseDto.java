package com.andersenlab.etalon.infoservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageResponseDto(String message, Object data) {
  public static final String DEPOSIT_CREATED = "Your deposit has been successfully created";
  public static final String PAYMENT_IS_SUCCESSFUL = "Payment has been successful";
  public static final String TRANSFER_OPERATION_IS_PROCESSING = "Operation is processing";

  public MessageResponseDto(String message) {
    this(message, null);
  }
}
