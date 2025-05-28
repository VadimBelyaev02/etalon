package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.Details;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "transactions")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

  @Id
  @SequenceGenerator(
      name = "transactions_sequence",
      sequenceName = "transactions_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "sender_account", nullable = false)
  private String senderAccount;

  @Column(name = "receiver_account", nullable = false)
  private String receiverAccount;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency")
  private CurrencyName currency;

  @Column(name = "standard_rate")
  private BigDecimal standardRate;

  @Column(name = "fee_amount")
  private BigDecimal feeAmount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransactionStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "details", nullable = false)
  private Details details;

  @Column(name = "name", nullable = false)
  private String transactionName;

  @Column(name = "created_at", nullable = false, updatable = false)
  private ZonedDateTime createAt = ZonedDateTime.now();

  @Column(name = "processed_at", updatable = false)
  private ZonedDateTime processedAt = ZonedDateTime.now();

  @Column(name = "sender_card_id")
  private Long senderCardId;

  @Column(name = "receiver_card_id")
  private Long receiverCardId;

  @OneToMany(mappedBy = "transactionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventEntity> eventEntityList = new ArrayList<>();
}
