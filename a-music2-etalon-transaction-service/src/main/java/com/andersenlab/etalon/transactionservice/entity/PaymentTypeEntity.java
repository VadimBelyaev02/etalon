package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.PaymentType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
@Table(name = "payment_types")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTypeEntity {
  @Id
  @SequenceGenerator(
      name = "payment_types_sequence",
      sequenceName = "payment_types_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_types_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private PaymentType type;

  @Column(name = "iban", nullable = false)
  private String iban;

  @OneToOne(mappedBy = "paymentProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private TaxEntity taxEntity;

  @OneToOne(mappedBy = "paymentProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private FineEntity fineEntity;
}
