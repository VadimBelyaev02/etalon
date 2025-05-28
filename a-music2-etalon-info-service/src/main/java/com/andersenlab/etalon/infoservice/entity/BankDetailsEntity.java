package com.andersenlab.etalon.infoservice.entity;

import com.andersenlab.etalon.infoservice.util.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bank_detail")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailsEntity {
  @Id private long id;

  @Column(nullable = false, unique = true)
  private String bin;

  @Column(name = "bank_code", nullable = false, unique = true)
  private String bankCode;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Currency currency;

  @ManyToOne
  @JoinColumn(name = "bank_id", nullable = false)
  private BankInfoEntity bankInfo;
}
