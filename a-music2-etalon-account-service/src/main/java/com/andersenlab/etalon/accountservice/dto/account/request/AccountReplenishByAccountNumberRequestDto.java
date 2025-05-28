package com.andersenlab.etalon.accountservice.dto.account.request;

import java.math.BigDecimal;

public record AccountReplenishByAccountNumberRequestDto(BigDecimal replenishAmount) {}
