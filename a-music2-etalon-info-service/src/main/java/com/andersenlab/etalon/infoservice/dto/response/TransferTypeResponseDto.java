package com.andersenlab.etalon.infoservice.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record TransferTypeResponseDto(Long id, String name, String transferType) {}
