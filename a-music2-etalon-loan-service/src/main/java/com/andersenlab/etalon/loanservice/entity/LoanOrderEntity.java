package com.andersenlab.etalon.loanservice.entity;

import com.andersenlab.etalon.loanservice.util.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Table(name = "orders")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoanOrderEntity extends UpdatableEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(name = "orders_sequence", sequenceName = "orders_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_sequence")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "average_monthly_salary", nullable = false)
  private BigDecimal averageMonthlySalary;

  @Column(name = "average_monthly_expenses", nullable = false)
  private BigDecimal averageMonthlyExpenses;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private OrderStatus status;

  @Column(name = "borrower", nullable = false)
  String borrower;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private LoanProductEntity product;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "order_to_guarantor",
      joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "guarantor_id"))
  private Set<GuarantorEntity> guarantors;
}
