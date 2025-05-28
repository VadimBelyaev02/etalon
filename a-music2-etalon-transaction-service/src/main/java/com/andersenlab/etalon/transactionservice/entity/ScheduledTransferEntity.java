package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.ScheduleFrequency;
import com.andersenlab.etalon.transactionservice.util.enums.ScheduleStatus;
import com.andersenlab.etalon.transactionservice.util.enums.TransferType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "scheduled_transfer")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransferEntity extends UpdatableEntity {
  @Id
  @SequenceGenerator(
      name = "scheduled_transfer_sequence",
      sequenceName = "scheduled_transfer_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduled_transfer_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "schedule_status", nullable = false)
  private ScheduleStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "schedule_frequency", nullable = false)
  private ScheduleFrequency frequency;

  @Enumerated(EnumType.STRING)
  @Column(name = "transfer_type", nullable = false)
  private TransferType transferType;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false)
  private CurrencyName currency;

  @Column(name = "sender_account_number", nullable = false)
  private String senderAccountNumber;

  @Column(name = "beneficiary_account_number", nullable = false)
  private String beneficiaryAccountNumber;

  @Column(name = "start_date", nullable = false)
  private ZonedDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private ZonedDateTime endDate;

  @Column(name = "next_transfer_date", nullable = false)
  private ZonedDateTime nextTransferDate;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @OneToMany(
      fetch = FetchType.EAGER,
      mappedBy = "scheduledTransfer",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<ScheduledTransferDateEntity> specificDates;
}
