package com.andersenlab.etalon.cardservice.entity;

import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import com.andersenlab.etalon.cardservice.util.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "products")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CardProductEntity {

  @Id
  @Column(name = "id")
  @SequenceGenerator(
      name = "products_sequence",
      sequenceName = "products_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "products_sequence")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "product_type", nullable = false)
  private ProductType productType;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "issuer", nullable = false)
  private Issuer issuer;

  @Column(name = "validity", nullable = false)
  private Integer validity;

  @Column(name = "issuance_fee", nullable = false)
  private BigDecimal issuanceFee;

  @Column(name = "annual_maintenance_fee", nullable = false)
  private BigDecimal maintenanceFee;

  @Column(name = "APR")
  private BigDecimal apr;

  @Column(name = "cashback")
  private BigDecimal cashback;

  @JsonManagedReference
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_to_currency",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "currency_id"))
  private List<CurrencyEntity> availableCurrencies;
}
