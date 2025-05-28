package com.andersenlab.etalon.infoservice.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionStatus {
  APPROVED,
  DECLINED,
  PROCESSING,
  CREATED;
}
