package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public final class FinePaymentTypeResponseDto extends PaymentTypeResponseDto {
  private final String fineType;
  private final String recipientName;
}
