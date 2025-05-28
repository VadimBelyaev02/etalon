package com.andersenlab.etalon.cardservice.entity;

import com.andersenlab.etalon.cardservice.util.enums.CardBlockingReason;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
public class CardEntity extends UpdatableEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(name = "card_sequence", sequenceName = "card_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_sequence")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "expiration_date", nullable = false)
  private ZonedDateTime expirationDate;

  @Column(nullable = false, unique = true)
  private String number;

  @Column(nullable = false)
  private String accountNumber;

  @Column(name = "blocked", nullable = false)
  private Boolean isBlocked;

  @Enumerated(EnumType.STRING)
  @Column(name = "blocking_reason")
  private CardBlockingReason blockingReason;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private CardStatus status;

  @Column(name = "cardholder_name", nullable = false)
  private String cardholderName;

  @Column(name = "cvv", nullable = false)
  private Integer cvv;

  @Column(name = "withdraw_limit", nullable = false)
  private BigDecimal withdrawLimit;

  @Column(name = "transfer_limit", nullable = false)
  private BigDecimal transferLimit;

  @Column(name = "daily_expense_limit", nullable = false)
  private BigDecimal dailyExpenseLimit;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "product_id", nullable = false)
  private CardProductEntity product;

  @Column(name = "bank_branch_id", nullable = false)
  private Long bankBranchId;
}
