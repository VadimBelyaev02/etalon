package com.andersenlab.etalon.infoservice.dto.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record EventResponseDto(
    Long id,
    String status,
    String type,
    BigDecimal amount,
    String accountNumber,
    String createAt,
    String name) {}
