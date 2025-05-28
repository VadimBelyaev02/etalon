package com.andersenlab.etalon.infoservice.entity;

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

@Getter
@Setter
@Entity
@Table(schema = "info", name = "locales")
@NoArgsConstructor
@AllArgsConstructor
public class LocaleEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "locales_sequence",
      sequenceName = "locales_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locales_sequence")
  private Long id;

  @Column(name = "code")
  private String code;
}
