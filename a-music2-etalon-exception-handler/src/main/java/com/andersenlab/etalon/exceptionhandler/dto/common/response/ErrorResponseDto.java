package com.andersenlab.etalon.exceptionhandler.dto.common.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record ErrorResponseDto(
    LocalDateTime timestamp,
    String traceId,
    String message,
    String serviceIdentifier,
    String errorCode,
    String description,
    List<ViolationDto> violations) {}
