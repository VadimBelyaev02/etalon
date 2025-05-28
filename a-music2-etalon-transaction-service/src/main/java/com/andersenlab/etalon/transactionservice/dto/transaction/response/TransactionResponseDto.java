package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record TransactionResponseDto(
    Long id, String status, String details, String transactionName, String createAt) {}
