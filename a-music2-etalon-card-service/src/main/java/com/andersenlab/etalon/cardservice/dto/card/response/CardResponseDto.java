package com.andersenlab.etalon.cardservice.dto.card.response;

import static com.andersenlab.etalon.cardservice.util.Constants.JSON_MASK_FULL_NAME_PATTERN;
import static com.andersenlab.etalon.cardservice.util.Constants.JSON_MASK_FULL_NAME_REPLACE_PATTERN;

import com.andersenlab.etalon.cardservice.annotations.JsonMask;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Currency;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import com.andersenlab.etalon.cardservice.util.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;
import lombok.Builder;

@Builder(toBuilder = true)
public record CardResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "2024-06-16T14:26:52Z") ZonedDateTime expirationDate,
    @Schema(example = "Visa") Issuer issuer,
    @Schema(example = "Debit") ProductType productType,
    @Schema(example = "Enjoy") String productName,
    @Schema(example = "************4321") String number,
    @Schema(example = "12.12") BigDecimal balance,
    @Schema(anyOf = Currency.class) Currency currency,
    @Schema(example = "PL04234567840000000000000001") String accountNumber,
    @Schema(example = "false") Boolean isBlocked,
    @Schema(example = "ACTIVE") CardStatus status,
    @Schema(example = "ROBERT S.")
        @JsonMask(
            replaceRegExp = JSON_MASK_FULL_NAME_PATTERN,
            replaceWith = JSON_MASK_FULL_NAME_REPLACE_PATTERN)
        String cardholderName,
    @Schema(example = "1000") BigDecimal withdrawLimit,
    @Schema(example = "2000") BigDecimal transferLimit,
    @Schema(example = "3000") BigDecimal dailyExpenseLimit,
    @Deprecated(since = "16.09.2024", forRemoval = true)
        @Schema(example = "{\"PL04234567840000000000000001\": \"PLN\"}", deprecated = true)
        Map<String, Currency> accounts) {}
