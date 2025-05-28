package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransactionMessageResponseDto(
    MessageResponseDto messageResponseDto, Long transactionId, String status) {}
