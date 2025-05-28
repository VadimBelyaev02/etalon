package com.andersenlab.etalon.depositservice.dto.transaction.response;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;

public record TransactionMessageResponseDto(
    MessageResponseDto messageResponseDto, Long transactionId) {}
