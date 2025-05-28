package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.FineType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fines")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FineEntity {

  @Id
  @Column(name = "payment_product_id")
  private Long id;

  @MapsId @OneToOne private PaymentTypeEntity paymentProduct;

  @Enumerated(EnumType.STRING)
  @Column(name = "fine_type", nullable = false)
  private FineType fineType;

  @Column(name = "recipient_name", nullable = false)
  private String recipientName;
}
