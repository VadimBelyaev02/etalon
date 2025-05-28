package com.andersenlab.etalon.transactionservice.dto.account.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountDetailedResponseDto(
    Long id,
    String iban,
    String userId,
    BigDecimal balance,
    Boolean isBlocked,
    CurrencyName currency) {}
