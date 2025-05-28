package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity extends UpdatableEntity {
  @Id
  @SequenceGenerator(
      name = "payments_sequence",
      sequenceName = "payments_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "payment_product_id", nullable = false)
  private Long paymentProductId;

  @Column(name = "transaction_id")
  private Long transactionId;

  @Column(name = "comment", nullable = false)
  private String comment;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "account_number_withdrawn", nullable = false)
  private String accountNumberWithdrawn;

  @Column(name = "is_template", nullable = false)
  private Boolean isTemplate;

  @Column(name = "template_name", nullable = false)
  private String templateName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PaymentStatus status;

  @Column(name = "destination")
  private String destination;
}
