package com.andersenlab.etalon.infoservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "atm")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AtmEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(name = "atm_sequence", sequenceName = "atm_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "atm_sequence")
  private Long id;

  @Column(name = "atm_name", nullable = false, unique = true)
  private String atmName;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "latitude", nullable = false)
  private String latitude;

  @Column(name = "longitude", nullable = false)
  private String longitude;

  @OneToMany(mappedBy = "atm")
  private List<AtmOperationModeEntity> atmOperationModes;
}
