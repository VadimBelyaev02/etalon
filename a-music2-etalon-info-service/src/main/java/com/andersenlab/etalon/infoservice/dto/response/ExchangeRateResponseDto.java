package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ExchangeRateResponseDto(
    CurrencyName currencyName,
    Long currencyId,
    BigDecimal sellingRate,
    BigDecimal buyingRate,
    ZonedDateTime updateAt) {}
