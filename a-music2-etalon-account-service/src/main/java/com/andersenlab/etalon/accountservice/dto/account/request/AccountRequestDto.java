package com.andersenlab.etalon.accountservice.dto.account.request;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record AccountRequestDto(BigDecimal balance) {}
