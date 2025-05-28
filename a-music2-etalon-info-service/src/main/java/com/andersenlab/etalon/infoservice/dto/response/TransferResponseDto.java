package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import com.andersenlab.etalon.infoservice.util.enums.TransferStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransferResponseDto(
    Long id,
    ZonedDateTime createAt,
    ZonedDateTime updateAt,
    TransferTypeResponseDto transferType,
    TransactionDetailedResponseDto transaction,
    String description,
    BigDecimal totalAmount,
    BigDecimal amount,
    BigDecimal fee,
    String senderFullName,
    String beneficiaryFullName,
    String senderAccountNumber,
    String beneficiaryAccountNumber,
    String beneficiaryBank,
    TransferStatus status,
    CurrencyName currency) {}
