package com.andersenlab.etalon.transactionservice.dto.common;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record FeeDto(BigDecimal amount, BigDecimal rate) {}
