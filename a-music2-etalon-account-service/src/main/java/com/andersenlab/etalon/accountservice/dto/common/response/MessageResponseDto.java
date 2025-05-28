package com.andersenlab.etalon.accountservice.dto.common.response;

public record MessageResponseDto(String message) {

  public static final String ACCOUNT_UPDATED_SUCCESSFULLY = "Account updated successfully";
  public static final String ACCOUNT_BALANCE_REPLENISHED_SUCCESSFULLY =
      "Account balance replenished successfully";
  public static final String ACCOUNT_BALANCE_WITHDRAWN_SUCCESSFULLY =
      "Account balance withdrawn successfully";
}
