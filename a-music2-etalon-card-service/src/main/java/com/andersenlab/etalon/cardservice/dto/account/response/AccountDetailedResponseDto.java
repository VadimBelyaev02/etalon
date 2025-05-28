package com.andersenlab.etalon.cardservice.dto.account.response;

import com.andersenlab.etalon.cardservice.util.enums.Currency;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountDetailedResponseDto(
    Long id,
    String iban,
    String userId,
    BigDecimal balance,
    Boolean isBlocked,
    Currency currency) {}
