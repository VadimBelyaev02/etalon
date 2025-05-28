package com.andersenlab.etalon.userservice.entity;

import com.andersenlab.etalon.userservice.util.ResetPasswordStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reset_password")
public class ResetPasswordEntity {

  @Id
  @SequenceGenerator(
      name = "reset_password_sequence",
      sequenceName = "reset_password_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reset_password_sequence")
  @Column(name = "reset_password_id")
  private Long resetPasswordId;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "token", nullable = false)
  private UUID token;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  private ZonedDateTime expiresAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  @Builder.Default
  private ResetPasswordStatus status = ResetPasswordStatus.PENDING;
}
