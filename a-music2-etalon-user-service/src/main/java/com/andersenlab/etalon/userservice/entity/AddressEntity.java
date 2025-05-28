package com.andersenlab.etalon.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {
  private String voivodeship;
  private String city;
  private String street;
  private String building;
  private String apartment;

  @Column(name = "post_code")
  private String postCode;
}
