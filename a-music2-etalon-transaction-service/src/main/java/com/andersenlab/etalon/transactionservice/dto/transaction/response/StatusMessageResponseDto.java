package com.andersenlab.etalon.transactionservice.dto.transaction.response;

public record StatusMessageResponseDto(Boolean status, String message) {
  public static final String TEMPLATE_NAME_IS_VALID = "Template name is valid";
}
