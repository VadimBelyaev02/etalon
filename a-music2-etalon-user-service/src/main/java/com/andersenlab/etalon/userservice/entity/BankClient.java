package com.andersenlab.etalon.userservice.entity;

import com.andersenlab.etalon.userservice.util.BankClientStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bank_clients")
public class BankClient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String pesel;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String email;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @Embedded private AddressEntity address;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private BankClientStatus status;
}
