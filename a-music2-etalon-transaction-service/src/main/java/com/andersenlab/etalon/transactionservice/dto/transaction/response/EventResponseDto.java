package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record EventResponseDto(
    Long id,
    String status,
    String type,
    BigDecimal amount,
    CurrencyName currency,
    String accountNumber,
    String createAt,
    String name,
    String eventType) {}
