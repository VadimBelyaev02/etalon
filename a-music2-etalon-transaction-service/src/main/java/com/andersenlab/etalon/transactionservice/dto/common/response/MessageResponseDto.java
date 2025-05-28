package com.andersenlab.etalon.transactionservice.dto.common.response;

public record MessageResponseDto(String message) {
  public static final String TRANSACTION_FAILED = "Transaction with id %s failed";
  public static final String OPERATION_IS_SUCCESSFUL = "Operation has been successful";
  public static final String OPERATION_IS_FAILED = "Operation has been failed";
  public static final String OPERATION_IS_PROCESSING = "Operation is processing";
  public static final String TRANSACTION_CREATED = "Transaction with id %s was created";
  public static final String TRANSACTION_IS_SUCCESSFUL = "Transaction with id %s was successful";
  public static final String TRANSACTION_IS_PROCESSING = "Transaction with id %s is processing";
  public static final String PAYMENT_SUCCEEDED = "Account balance has been changed";
  public static final String TEMPLATE_DELETE_IS_SUCCESSFUL =
      "Template with id %s deleted successfully";
  public static final String TEMPLATE_PATCH_IS_SUCCESSFUL =
      "Template with id %s updated successfully";
}
