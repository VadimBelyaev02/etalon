package com.andersenlab.etalon.transactionservice.entity;

import com.andersenlab.etalon.transactionservice.util.enums.TemplateType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "templates")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TemplateEntity extends CreatableEntity {
  @Id
  @SequenceGenerator(
      name = "templates_sequence",
      sequenceName = "templates_sequence",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "templates_sequence")
  @Column(name = "template_id")
  private Long templateId;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(name = "template_name", nullable = false)
  private String templateName;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "amount", nullable = false)
  private BigDecimal amount;

  @Column(name = "source")
  private String source;

  @Column(name = "destination")
  private String destination;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TemplateType templateType;
}
