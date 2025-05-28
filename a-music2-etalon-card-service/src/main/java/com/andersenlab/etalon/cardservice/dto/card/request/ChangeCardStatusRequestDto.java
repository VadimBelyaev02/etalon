package com.andersenlab.etalon.cardservice.dto.card.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
public record ChangeCardStatusRequestDto(
    @Schema(example = "101") Long id, @Schema(example = "LOST") String blockingReason) {}
