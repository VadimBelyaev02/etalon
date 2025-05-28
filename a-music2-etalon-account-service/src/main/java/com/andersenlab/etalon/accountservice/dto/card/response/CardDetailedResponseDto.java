package com.andersenlab.etalon.accountservice.dto.card.response;

import com.andersenlab.etalon.accountservice.util.enums.AccountStatus;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Builder;

@Builder(toBuilder = true)
public record CardDetailedResponseDto(
    @Schema(
            example =
                "{\"PL59234567840000000000000043\": \"PLN\", \"PL58234567840000000000000044\": \"USD\"}")
        Map<String, Currency> accounts,
    @Schema(example = "ACTIVE") AccountStatus status,
    @Schema(example = "ROBERT SMITH") String cardholderName) {}
