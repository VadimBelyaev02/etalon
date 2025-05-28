package com.andersenlab.etalon.depositservice.entity;

import com.andersenlab.etalon.depositservice.util.enums.Currency;
import com.andersenlab.etalon.depositservice.util.enums.Term;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Table(name = "products")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepositProductEntity extends UpdatableEntity {
  @Id
  @SequenceGenerator(
      name = "products_sequence",
      sequenceName = "products_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_sequence")
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "min_deposit_period", nullable = false)
  private BigDecimal minDepositPeriod;

  @Column(name = "max_deposit_period", nullable = false)
  private BigDecimal maxDepositPeriod;

  @Enumerated(EnumType.STRING)
  @Column(name = "term", nullable = false)
  private Term term;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false)
  private Currency currency;

  @Column(name = "interest_rate", nullable = false)
  private BigDecimal interestRate;

  @Column(name = "min_open_amount", nullable = false)
  private BigDecimal minOpenAmount;

  @Column(name = "max_deposit_amount", nullable = false)
  private BigDecimal maxDepositAmount;

  @Column(name = "early_withdrawal", nullable = false)
  private Boolean isEarlyWithdrawal;
}
