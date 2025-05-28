package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
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
@Table(name = "transfers")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransferEntity extends UpdatableEntity {
  @Id
  @SequenceGenerator(
      name = "transfers_sequence",
      sequenceName = "transfers_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfers_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "transfer_type_id")
  private Long transferTypeId;

  @Column(name = "transaction_id")
  private Long transactionId;

  @Column(name = "comment", nullable = false)
  private String comment;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "source", nullable = false)
  private String source;

  @Column(name = "destination", nullable = false)
  private String destination;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TransferStatus status;

  @Column(name = "is_template")
  private Boolean isTemplate;

  @Column(name = "template_name")
  private String templateName;

  @Column(name = "is_fee_provided", nullable = false)
  private Boolean isFeeProvided;

  @Column(name = "fee", updatable = false)
  private BigDecimal fee;

  @Column(name = "fee_rate", updatable = false)
  private BigDecimal feeRate;

  @Column(name = "standard_rate", updatable = false)
  private BigDecimal standardRate;
}
