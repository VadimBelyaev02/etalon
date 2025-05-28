package com.andersenlab.etalon.depositservice.entity;

import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "deposits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DepositEntity extends UpdatableEntity {

  @Id
  @SequenceGenerator(
      name = "deposit_sequence",
      sequenceName = "deposit_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposit_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "duration", nullable = false)
  private Integer duration;

  @Column(name = "account_number", nullable = false)
  private String accountNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DepositStatus status;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private DepositProductEntity product;

  @Column(name = "interest_account_number", nullable = false)
  private String interestAccountNumber;

  @Column(name = "final_transfer_account_number", nullable = false)
  private String finalTransferAccountNumber;
}
