package com.andersenlab.etalon.infoservice.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Type {
  INCOME("Incoming"),
  OUTCOME("Outgoing"),
  FEE("Fee");

  private final String name;
}
