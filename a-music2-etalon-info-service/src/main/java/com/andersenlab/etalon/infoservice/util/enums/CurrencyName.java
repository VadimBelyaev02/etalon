package com.andersenlab.etalon.infoservice.util.enums;

import lombok.Getter;

@Getter
public enum CurrencyName {
  PLN(1L, "zł"),
  USD(2L, "$"),
  EUR(3L, "€"),
  GBP(4L, "£"),
  CHF(5L, "₣");

  private final Long id;
  private final String currencySymbol;

  CurrencyName(Long id, String currencySymbol) {
    this.id = id;
    this.currencySymbol = currencySymbol;
  }
}
