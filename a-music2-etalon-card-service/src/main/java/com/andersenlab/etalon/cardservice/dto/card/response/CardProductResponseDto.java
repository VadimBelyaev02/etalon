package com.andersenlab.etalon.cardservice.dto.card.response;

import com.andersenlab.etalon.cardservice.util.enums.Currency;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import com.andersenlab.etalon.cardservice.util.enums.ProductType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CardProductResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "Etalon") String name,
    @Schema(example = "VISA") Issuer issuer,
    @Schema(example = "DEBIT") ProductType productType,
    @Schema(example = "[\"PLN\"]") List<Currency> availableCurrencies,
    @Schema(example = "3") Integer validity,
    @Schema(example = "0") BigDecimal issuanceFee,
    @Schema(example = "40") BigDecimal maintenanceFee,
    @Schema(example = "15") BigDecimal apr,
    @Schema(example = "0") BigDecimal cashback) {}
