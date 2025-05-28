package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.EventType;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransactionDetailedResponseDto(
    Long id,
    String transactionDate,
    String transactionTime,
    String transactionName,
    String outcomeAccountNumber,
    String incomeAccountNumber,
    BigDecimal transactionAmount,
    CurrencyName currency,
    Type type,
    TransactionStatus status,
    EventType eventType) {}
