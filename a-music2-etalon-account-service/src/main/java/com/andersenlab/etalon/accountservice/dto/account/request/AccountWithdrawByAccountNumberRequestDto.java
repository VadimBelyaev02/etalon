package com.andersenlab.etalon.accountservice.dto.account.request;

import java.math.BigDecimal;

public record AccountWithdrawByAccountNumberRequestDto(BigDecimal withdrawAmount) {}
