package com.andersenlab.etalon.transactionservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "scheduled_transfer_date")
@Getter
@Setter
public class ScheduledTransferDateEntity {
  @Id
  @SequenceGenerator(
      name = "scheduled_transfer_date_sequence",
      sequenceName = "scheduled_transfer_date_sequence",
      allocationSize = 1)
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "scheduled_transfer_date_sequence")
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "scheduled_transfer_id", nullable = false)
  private ScheduledTransferEntity scheduledTransfer;

  @Column(name = "transfer_date", nullable = false)
  private LocalDate transferDate;
}
