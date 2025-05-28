package com.andersenlab.etalon.infoservice.entity;

import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "auth_confirmations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ConfirmationEntity extends UpdatableEntity {
  @Id
  @SequenceGenerator(
      name = "auth_confirmations_sequence",
      sequenceName = "auth_confirmations_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_confirmations_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "invalid_attempts", nullable = false)
  private Integer invalidAttempts;

  @Enumerated(EnumType.STRING)
  @Column(name = "confirmation_method", nullable = false)
  private ConfirmationMethod confirmationMethod;

  @Column(name = "confirmation_code", nullable = false)
  private Integer confirmationCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "operation", nullable = false)
  private Operation operation;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ConfirmationStatus status;

  @Column(name = "blocked_at")
  private ZonedDateTime blockedAt;

  @Column(name = "blocked_until")
  private ZonedDateTime blockedUntil;

  @PrePersist
  public void prePersist() {
    invalidAttempts = 0;
  }

  public void incrementInvalidAttempts() {
    invalidAttempts += 1;
  }
}
