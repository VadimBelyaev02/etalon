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
@Table(name = "bank_branches")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankBranchEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "bank_branches_sequence",
      sequenceName = "bank_branches_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_branches_sequence")
  private Long id;

  @Column(name = "bank_branch_name", nullable = false, unique = true)
  private String bankBranchName;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "latitude", nullable = false)
  private String latitude;

  @Column(name = "longitude", nullable = false)
  private String longitude;

  @OneToMany(mappedBy = "bankBranch")
  private List<BankBranchOperationModeEntity> bankBranchOperationModes;
}
