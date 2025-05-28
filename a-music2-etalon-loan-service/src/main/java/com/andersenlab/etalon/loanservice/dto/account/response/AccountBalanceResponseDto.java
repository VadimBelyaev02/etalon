package com.andersenlab.etalon.loanservice.dto.account.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AccountBalanceResponseDto(BigDecimal accountBalance) {}
