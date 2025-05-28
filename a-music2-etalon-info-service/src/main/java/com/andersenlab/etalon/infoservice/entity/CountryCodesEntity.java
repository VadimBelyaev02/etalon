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

@Entity
@Getter
@Setter
@Table(name = "country_codes")
@NoArgsConstructor
@AllArgsConstructor
public class CountryCodesEntity {
  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "country_codes_sequence",
      sequenceName = "country_codes_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_codes_sequence")
  private Long id;

  @Column(name = "country_name_en", nullable = false, unique = true)
  private String countryNameEn;

  @Column(name = "country_name_pl", nullable = false, unique = true)
  private String countryNamePl;

  @Column(name = "phone_code", nullable = false)
  private String phoneCode;

  @Column(name = "image_key", nullable = false, unique = true)
  private String imageKey;
}
