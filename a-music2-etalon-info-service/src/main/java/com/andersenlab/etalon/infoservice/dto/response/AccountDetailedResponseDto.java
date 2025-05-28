package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.Currency;
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
