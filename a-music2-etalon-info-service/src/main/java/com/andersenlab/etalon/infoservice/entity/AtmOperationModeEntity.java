package com.andersenlab.etalon.infoservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "atm_operation_mode")
public class AtmOperationModeEntity extends AbstractOperationModeEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "atm_operation_mode_sequence",
      sequenceName = "atm_operation_mode_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atm_operation_mode_sequence")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "atm_id", nullable = false)
  private AtmEntity atm;
}
