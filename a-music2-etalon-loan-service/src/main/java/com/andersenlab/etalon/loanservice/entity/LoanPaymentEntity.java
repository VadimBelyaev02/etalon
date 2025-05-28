package com.andersenlab.etalon.loanservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "loan_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPaymentEntity extends UpdatableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loan_id", nullable = false)
  private LoanEntity loan;

  @Column(name = "principal_payment_amount", nullable = false)
  private BigDecimal principalPaymentAmount;

  @Column(name = "accrued_interest", nullable = false)
  private BigDecimal accruedInterest;

  @Column(name = "accrued_commission", nullable = false)
  private BigDecimal accruedCommission;

  @Column(name = "penalty")
  private BigDecimal penalty;

  @Column(name = "total_payment_amount", nullable = false)
  private BigDecimal totalPaymentAmount;
}
