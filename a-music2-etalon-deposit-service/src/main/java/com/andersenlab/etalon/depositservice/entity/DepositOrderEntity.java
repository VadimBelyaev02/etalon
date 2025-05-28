package com.andersenlab.etalon.depositservice.entity;

import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "deposit_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DepositOrderEntity extends UpdatableEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "deposit_orders_sequence",
      sequenceName = "deposit_orders_sequence",
      allocationSize = 1,
      initialValue = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposit_orders_sequence")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "deposit_period", nullable = false)
  private Integer depositPeriod;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private DepositStatus status;

  @Column(name = "source_account", nullable = false)
  private String sourceAccount;

  @Column(name = "interest_account", nullable = false)
  private String interestAccount;

  @Column(name = "final_transfer_account", nullable = false)
  private String finalTransferAccount;

  @Column(name = "transaction_id")
  private Long transactionId;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private DepositProductEntity product;
}
