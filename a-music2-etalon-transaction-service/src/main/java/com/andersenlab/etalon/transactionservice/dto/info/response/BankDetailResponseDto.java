package com.andersenlab.etalon.transactionservice.dto.info.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import io.swagger.v3.oas.annotations.media.Schema;

public record BankDetailResponseDto(
    @Schema(example = "415046") String bin,
    @Schema(example = "315046") String bankCode,
    CurrencyName currency) {}
