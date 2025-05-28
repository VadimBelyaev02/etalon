package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.EventStatus;
import com.andersenlab.etalon.transactionservice.util.enums.EventType;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "events")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {

  @Id
  @SequenceGenerator(name = "events_sequence", sequenceName = "events_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_sequence")
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private EventStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private Type type;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency")
  private CurrencyName currency;

  @Column(name = "account_number", nullable = false)
  private String accountNumber;

  @Column(name = "created_at", nullable = false, updatable = false)
  private ZonedDateTime createAt = ZonedDateTime.now();

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type")
  private EventType eventType;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id", nullable = false)
  private TransactionEntity transactionEntity;
}
