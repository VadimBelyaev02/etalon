package com.andersenlab.etalon.cardservice.dto.card.request;

import com.andersenlab.etalon.cardservice.util.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CardCreationRequestDto(
    @Schema(example = "7") Long cardProductId,
    @Schema(example = "1") Long bankBranchId,
    @Schema(anyOf = Currency.class) List<Currency> currencies) {}
