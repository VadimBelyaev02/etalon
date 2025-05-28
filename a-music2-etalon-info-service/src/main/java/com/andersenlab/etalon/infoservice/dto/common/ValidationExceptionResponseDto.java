package com.andersenlab.etalon.infoservice.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

public record ValidationExceptionResponseDto(
    @Schema(example = "response message") String message,
    @Schema(example = "response parameter") String parameter) {}
