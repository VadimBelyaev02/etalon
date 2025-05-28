package com.andersenlab.etalon.accountservice.entity;

import com.andersenlab.etalon.accountservice.util.enums.AccountStatus;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import com.andersenlab.etalon.accountservice.util.enums.Type;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class AccountEntity extends UpdatableEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "accounts_sequence",
      sequenceName = "accounts_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_sequence")
  Long id;

  @Column(name = "user_id", nullable = false)
  String userId;

  @Column(name = "iban", nullable = false, unique = true)
  String iban;

  @Column(name = "balance", nullable = false)
  private BigDecimal balance;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Currency currency;

  @Column(name = "blocked", nullable = false)
  private Boolean isBlocked;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private AccountStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "account_type")
  private Type accountType;
}
