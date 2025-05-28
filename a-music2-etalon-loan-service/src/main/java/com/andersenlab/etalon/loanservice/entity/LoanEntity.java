package com.andersenlab.etalon.loanservice.entity;

import com.andersenlab.etalon.loanservice.util.enums.LoanStatus;
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
@Table(name = "loans")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity extends UpdatableEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(name = "loans_sequence", sequenceName = "loans_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loans_sequence")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "contract_number", nullable = false, unique = true)
  private String contractNumber;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "next_payment_date", nullable = false)
  private ZonedDateTime nextPaymentDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private LoanStatus status;

  @Column(name = "account_number", nullable = false)
  private String accountNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private LoanProductEntity product;
}
