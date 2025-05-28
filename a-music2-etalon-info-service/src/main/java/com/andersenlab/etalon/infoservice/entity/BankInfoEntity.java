package com.andersenlab.etalon.infoservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "bank_info")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankInfoEntity {
  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "bank_info_sequence",
      sequenceName = "bank_info_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_info_sequence")
  private Long id;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "call_name", nullable = false)
  private String callName;

  @OneToMany(mappedBy = "bankInfo", fetch = FetchType.EAGER)
  private List<BankDetailsEntity> bankDetails;
}
