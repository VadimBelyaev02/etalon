package com.andersenlab.etalon.cardservice.util.enums;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CurrencyLimit {
  PLN_WITHDRAW_LIMIT(new BigDecimal(200_000)),
  PLN_TRANSFER_LIMIT(new BigDecimal(100_000)),
  PLN_DAILY_EXPENSE_LIMIT(new BigDecimal(100_000)),
  EUR_USD_WITHDRAW_LIMIT(new BigDecimal(10_000)),
  EUR_USD_TRANSFER_LIMIT(new BigDecimal(5_000)),
  EUR_USD_DAILY_EXPENSE_LIMIT(new BigDecimal(5_000));

  private final BigDecimal limitValue;
}
