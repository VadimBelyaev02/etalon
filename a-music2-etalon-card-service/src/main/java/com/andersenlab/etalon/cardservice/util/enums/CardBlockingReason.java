package com.andersenlab.etalon.cardservice.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardBlockingReason {
  DAMAGED(1L, "The card has been damaged"),
  LOST(2L, "The card has been lost"),
  STOLEN_FRAUD(3L, "The card has been stolen or fraud");

  private final Long id;
  private final String reason;
}
