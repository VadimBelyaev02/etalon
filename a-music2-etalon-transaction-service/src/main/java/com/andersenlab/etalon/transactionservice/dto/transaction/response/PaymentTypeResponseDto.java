package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public sealed class PaymentTypeResponseDto
    permits TaxPaymentTypeResponseDto, FinePaymentTypeResponseDto {
  private final Long id;
  private final String subType;
  private final String name;
  private final String type;
  private final String iban;
  private final BigDecimal fee;
}
