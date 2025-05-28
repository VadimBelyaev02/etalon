package com.andersenlab.etalon.loanservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@Table(name = "guarantors")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GuarantorEntity extends UpdatableEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "guarantors_sequence",
      sequenceName = "guarantors_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guarantors_sequence")
  private Long id;

  @Column(name = "pesel", nullable = false, unique = true)
  private String pesel;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;
}
