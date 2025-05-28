package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateTransferResponseDto(Long confirmationId, String message) {}
