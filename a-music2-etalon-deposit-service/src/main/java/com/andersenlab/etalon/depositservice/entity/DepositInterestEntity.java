package com.andersenlab.etalon.depositservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "deposit_interests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DepositInterestEntity extends CreatableEntity {
  @Id
  @SequenceGenerator(
      name = "deposit_interests_sequence",
      sequenceName = "deposit_interests_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposit_interests_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "deposit_id", nullable = false)
  private Long depositId;

  @Column(name = "interest_amount", nullable = false)
  private BigDecimal interestAmount;
}
