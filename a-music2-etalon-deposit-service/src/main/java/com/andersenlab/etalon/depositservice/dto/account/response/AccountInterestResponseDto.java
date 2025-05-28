package com.andersenlab.etalon.depositservice.dto.account.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountInterestResponseDto(Long id, String accountNumber, BigDecimal balance) {}
