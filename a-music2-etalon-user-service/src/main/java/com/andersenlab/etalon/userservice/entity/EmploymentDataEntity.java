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
public class EmploymentDataEntity {
  private String position;

  @Column(name = "place_of_work")
  private String placeOfWork;
}
