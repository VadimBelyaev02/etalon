package com.andersenlab.etalon.transactionservice.dto.info.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ExchangeRateResponseDto(
    CurrencyName currencyName,
    @Schema(example = "1") Long currencyId,
    @Schema(example = "0.98") BigDecimal sellingRate,
    @Schema(example = "1.02") BigDecimal buyingRate,
    @Schema(example = "2023-07-10T21:58:14.5370354+02:00") ZonedDateTime updateAt) {}
