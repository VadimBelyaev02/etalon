package com.andersenlab.etalon.loanservice.dto.account.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountDetailedResponseDto(
    Long id, String iban, String userId, BigDecimal balance, Boolean isBlocked) {}
