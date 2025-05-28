package com.andersenlab.etalon.transactionservice.dto.card.response;

import com.andersenlab.etalon.transactionservice.util.enums.CardStatus;
import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.Issuer;
import com.andersenlab.etalon.transactionservice.util.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder(toBuilder = true)
public record CardResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "2024-06-16T14:26:52Z") ZonedDateTime expirationDate,
    @Schema(example = "Visa") Issuer issuer,
    @Schema(anyOf = ProductType.class) ProductType productType,
    @Schema(example = "Enjoy") String productName,
    @Schema(example = "1234123443214321") String number,
    @Schema(example = "12.12") BigDecimal balance,
    @Schema(anyOf = CurrencyName.class) List<CurrencyName> availableCurrencies,
    @Schema(example = "{\"414142142142124\": \"PLN\", \"414142142145554\": \"USD\"}")
        Map<String, CurrencyName> accounts,
    @Schema(example = "false") Boolean isBlocked,
    @Schema(anyOf = CardStatus.class) CardStatus status,
    @Schema(example = "ROBERT SMITH") String cardholderName,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    BigDecimal withdrawLimit,
    BigDecimal transferLimit,
    BigDecimal dailyExpenseLimit) {}
