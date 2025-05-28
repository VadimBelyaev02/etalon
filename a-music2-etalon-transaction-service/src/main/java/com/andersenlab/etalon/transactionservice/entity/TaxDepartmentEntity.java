package com.andersenlab.etalon.transactionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tax_departments")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaxDepartmentEntity {
  @Id
  @SequenceGenerator(
      name = "tax_departments_sequence",
      sequenceName = "tax_departments_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tax_departments_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;
}
