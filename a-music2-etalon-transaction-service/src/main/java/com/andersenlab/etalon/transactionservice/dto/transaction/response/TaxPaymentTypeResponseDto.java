package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public final class TaxPaymentTypeResponseDto extends PaymentTypeResponseDto {
  private final String taxType;
  private final String recipientName;
  private final String taxDepartmentName;
}
