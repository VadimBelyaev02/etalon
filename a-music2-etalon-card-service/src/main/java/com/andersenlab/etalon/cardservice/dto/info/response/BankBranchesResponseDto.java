package com.andersenlab.etalon.cardservice.dto.info.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record BankBranchesResponseDto(
    @Schema(example = "1") Long id,
    String name,
    @Schema(example = "Lublin") String city,
    @Schema(example = "Jaśkowa Dolina 132") String address) {}
