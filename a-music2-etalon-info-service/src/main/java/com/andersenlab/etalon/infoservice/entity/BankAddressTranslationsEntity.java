package com.andersenlab.etalon.infoservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "bank_address_translations",
    uniqueConstraints = @UniqueConstraint(columnNames = {"bank_contact_id", "locale"}))
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankAddressTranslationsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "bank_contact_id", referencedColumnName = "id", nullable = false)
  private BankContactsEntity bankContacts;

  @Column(name = "locale", length = 2, nullable = false)
  private String locale;

  @Column(name = "value", columnDefinition = "TEXT", nullable = false)
  private String value;
}
