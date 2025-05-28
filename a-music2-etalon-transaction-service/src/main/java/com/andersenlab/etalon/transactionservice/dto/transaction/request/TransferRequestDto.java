package com.andersenlab.etalon.transactionservice.dto.transaction.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransferRequestDto(
    String source,
    String destination,
    BigDecimal amount,
    String comment,
    Long transferTypeId,
    Boolean isTemplate,
    String templateName) {}
