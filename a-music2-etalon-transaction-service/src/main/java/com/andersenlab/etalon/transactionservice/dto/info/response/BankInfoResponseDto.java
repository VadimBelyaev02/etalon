package com.andersenlab.etalon.transactionservice.dto.info.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record BankInfoResponseDto(
    @Schema(example = "true") boolean isForeignBank,
    @Schema(example = "mBank S.A.") String fullName,
    @Schema(example = "mBank") String callName,
    @Schema(implementation = BankDetailResponseDto.class)
        List<BankDetailResponseDto> bankDetails) {}
