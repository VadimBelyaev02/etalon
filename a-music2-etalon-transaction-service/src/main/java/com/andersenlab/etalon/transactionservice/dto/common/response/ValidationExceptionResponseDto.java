package com.andersenlab.etalon.transactionservice.dto.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ValidationExceptionResponseDto(
    @Schema(example = "response message") String message,
    @Schema(example = "response parameter") String parameter) {}
