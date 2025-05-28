package com.andersenlab.etalon.userservice.entity;

import com.andersenlab.etalon.userservice.util.EmailModificationStatus;
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
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_modification")
public class EmailModificationEntity {
  @Id
  @SequenceGenerator(
      name = "email_modification_sequence",
      sequenceName = "email_modification_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_modification_sequence")
  @Column(name = "id")
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "new_email", nullable = false)
  private String newEmail;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private EmailModificationStatus status;
}
