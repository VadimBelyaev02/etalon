package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.EventType;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record EventDto(
    Type type,
    EventType eventType,
    BigDecimal amount,
    String accountNumber,
    CurrencyName currency) {}
