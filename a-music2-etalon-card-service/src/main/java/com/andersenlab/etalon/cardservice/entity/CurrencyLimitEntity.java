package com.andersenlab.etalon.cardservice.entity;

import com.andersenlab.etalon.cardservice.util.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "currency_limits")
public class CurrencyLimitEntity {
  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "currency_code", length = 3)
  private Currency currencyCode;

  @Column(name = "withdraw_limit", nullable = false)
  private BigDecimal withdrawLimit;

  @Column(name = "transfer_limit", nullable = false)
  private BigDecimal transferLimit;

  @Column(name = "daily_expense_limit", nullable = false)
  private BigDecimal dailyExpenseLimit;
}
