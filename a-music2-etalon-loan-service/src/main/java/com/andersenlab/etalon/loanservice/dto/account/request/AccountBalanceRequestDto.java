package com.andersenlab.etalon.loanservice.dto.account.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AccountBalanceRequestDto(BigDecimal balance) {}
