package com.andersenlab.etalon.cardservice.dto.common.response;

public record MessageResponseDto(String message) {

  public static final String CARD_BLOCK = "Card is blocked";
  public static final String CARD_UNBLOCK = "Card is unblocked";
}
