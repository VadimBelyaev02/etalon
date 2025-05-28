package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.TransferStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record TransferResponseDto(
    Long id,
    TransferTypeResponseDto transferType,
    TransactionDetailedResponseDto transaction,
    String description,
    BigDecimal totalAmount,
    BigDecimal amount,
    CurrencyName currency,
    BigDecimal fee,
    String senderFullName,
    String beneficiaryFullName,
    String senderAccountNumber,
    String beneficiaryAccountNumber,
    String beneficiaryBank,
    TransferStatus status,
    ZonedDateTime createAt,
    ZonedDateTime updateAt) {}
