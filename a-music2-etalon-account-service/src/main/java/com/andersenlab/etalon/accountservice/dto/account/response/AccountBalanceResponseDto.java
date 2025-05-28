package com.andersenlab.etalon.accountservice.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AccountBalanceResponseDto(@Schema(example = "1424.11") BigDecimal accountBalance) {}
