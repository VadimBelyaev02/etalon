package com.andersenlab.etalon.transactionservice.dto.card.request;

import com.andersenlab.etalon.transactionservice.util.enums.Details;
import java.math.BigDecimal;

public record CardLimitValidationDto(BigDecimal amount, Details details) {}
