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
@Table(name = "bank_contacts")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankContactsEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "bank_contacts_sequence",
      sequenceName = "bank_contacts_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bank_contacts_sequence")
  private Long id;

  @Column(name = "bank_name", nullable = false, unique = true)
  private String bankName;

  @Column(name = "nip", nullable = false, unique = true)
  private String nip;

  @Column(name = "regon", nullable = false, unique = true)
  private String regon;

  @Column(name = "swift_code", nullable = false, unique = true)
  private String swiftCode;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "phone_number", nullable = false, unique = true)
  private String phoneNumber;

  @OneToMany(mappedBy = "bankContacts")
  private List<BankContactsOperationModeEntity> bankContactsOperationModes;

  @OneToMany(mappedBy = "bankContacts")
  private List<BankAddressTranslationsEntity> bankAddressTranslations;
}
