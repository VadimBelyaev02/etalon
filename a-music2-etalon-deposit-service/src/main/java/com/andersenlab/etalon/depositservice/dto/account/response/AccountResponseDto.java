package com.andersenlab.etalon.depositservice.dto.account.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountResponseDto(Long id, String iban, BigDecimal balance) {}
