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
@Table(name = "bank_branches_operation_mode")
public class BankBranchOperationModeEntity extends AbstractOperationModeEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "bank_branches_operation_mode_sequence",
      sequenceName = "bank_branches_operation_mode_sequence",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "bank_branches_operation_mode_sequence")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bank_branches_id", nullable = false)
  private BankBranchEntity bankBranch;
}
