package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record CreateNewTransferResponseDto(
    Long transferId,
    String sender,
    String beneficiary,
    BigDecimal amount,
    BigDecimal fee,
    BigDecimal feeRate,
    BigDecimal standardRate,
    BigDecimal totalAmount,
    String description) {}
