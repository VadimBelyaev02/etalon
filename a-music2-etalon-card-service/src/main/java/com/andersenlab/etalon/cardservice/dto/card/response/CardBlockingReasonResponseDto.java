package com.andersenlab.etalon.cardservice.dto.card.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
public record CardBlockingReasonResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "TEMPORARY") String reason,
    @Schema(example = "Temporary blocking") String description) {}
