package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.TransactionStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder(toBuilder = true)
public record TransactionExtendedResponseDto(
    Long id,
    String name,
    TransactionStatus status,
    Type type,
    BigDecimal totalAmount,
    CurrencyName currency,
    String accountNumber,
    ShortCardInfoDto shortCardInfo,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime createdAt,
    @JsonInclude(JsonInclude.Include.NON_NULL) List<EventDto> events) {}
