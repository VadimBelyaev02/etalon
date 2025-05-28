package com.andersenlab.etalon.depositservice.dto.common.response;

public record MessageResponseDto(String message) {
  public static final String DEPOSIT_REPLENISH_SUCCESSFUL =
      "Deposit replenish has been made successfully";
  public static final String DEPOSIT_WITHDRAWAL_SUCCESSFUL =
      "Deposit withdraw has been made successfully";
  public static final String DEPOSIT_CLOSED_SUCCESSFULLY =
      "The deposit has been successfully closed. "
          + "To activate the deposit, top up this deposit with the amount specified in the terms of this deposit";
  public static final String DEPOSIT_IN_PROGRESS = "Deposit processing in progress";
  public static final String DEPOSIT_PATCH_IS_SUCCESSFUL =
      "Deposit with id %s updated successfully";
}
