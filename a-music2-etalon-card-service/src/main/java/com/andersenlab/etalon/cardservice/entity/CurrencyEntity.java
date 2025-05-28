package com.andersenlab.etalon.cardservice.entity;

import com.andersenlab.etalon.cardservice.util.enums.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currencies")
public class CurrencyEntity {
  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "currencies_sequence",
      sequenceName = "currencies_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currencies_sequence")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency_code", nullable = false)
  private Currency currencyCode;

  @Column(name = "currency_name", nullable = false)
  private String currencyName;

  @JsonBackReference
  @ManyToMany(mappedBy = "availableCurrencies")
  private List<CardProductEntity> cardProducts;
}
