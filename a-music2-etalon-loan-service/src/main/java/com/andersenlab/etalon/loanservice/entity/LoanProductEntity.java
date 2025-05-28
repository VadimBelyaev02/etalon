package com.andersenlab.etalon.loanservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
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
public class LoanProductEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "products_sequence",
      sequenceName = "products_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_sequence")
  private Long id;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "duration", nullable = false)
  private Integer duration;

  @Column(name = "APR", nullable = false)
  private BigDecimal apr;

  @Column(name = "guarantors", nullable = false)
  private Integer requiredGuarantors;

  @Column(name = "min_amount", nullable = false)
  private BigDecimal minAmount;

  @Column(name = "max_amount", nullable = false)
  private BigDecimal maxAmount;

  @Column(name = "monthly_commission", nullable = false)
  private BigDecimal monthlyCommission;

  @OneToMany(mappedBy = "product")
  private List<LoanEntity> loanEntities;
}
