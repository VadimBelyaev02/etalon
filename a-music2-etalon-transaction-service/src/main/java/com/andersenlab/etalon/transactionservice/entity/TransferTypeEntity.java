package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.TransferType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transfer_types")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransferTypeEntity {
  @Id
  @SequenceGenerator(
      name = "transfer_types_sequence",
      sequenceName = "transfer_types_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transfer_types_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TransferType transferType;
}
