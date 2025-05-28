package com.andersenlab.etalon.transactionservice.util.enums;

public enum CurrencyName {
  PLN(1L),
  USD(2L),
  EUR(3L),
  GBP(4L),
  CHF(5L);

  private final Long id;

  CurrencyName(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }
}
