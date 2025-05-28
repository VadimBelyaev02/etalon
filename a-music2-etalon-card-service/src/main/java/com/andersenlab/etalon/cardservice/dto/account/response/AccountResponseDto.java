package com.andersenlab.etalon.cardservice.dto.account.response;

import com.andersenlab.etalon.cardservice.util.enums.AccountType;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record AccountResponseDto(
    Long id,
    String iban,
    BigDecimal balance,
    Currency currency,
    ZonedDateTime expirationDate,
    Boolean isBlocked,
    CardStatus status,
    AccountType type) {}
