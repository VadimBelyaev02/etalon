package com.andersenlab.etalon.userservice.entity;

import com.andersenlab.etalon.userservice.util.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registration_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String pesel;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Column(unique = true, nullable = true)
  private String registrationId;
}
