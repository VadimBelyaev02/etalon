package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.TaxType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "taxes")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaxEntity {
  @Id
  @Column(name = "payment_product_id")
  private Long id;

  @MapsId @OneToOne private PaymentTypeEntity paymentProduct;

  @Enumerated(EnumType.STRING)
  @Column(name = "tax_type", nullable = false)
  private TaxType taxType;

  @Column(name = "recipient_name", nullable = false)
  private String recipientName;

  @ManyToOne
  @JoinColumn(name = "tax_department_id", nullable = false)
  private TaxDepartmentEntity taxDepartmentEntity;
}
