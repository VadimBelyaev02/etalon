package com.andersenlab.etalon.transactionservice.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
  INCOME("Incoming"),
  OUTCOME("Outgoing");
  private final String name;
}
