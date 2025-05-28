package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record TransferTypeResponseDto(Long id, String name, String transferType) {}
