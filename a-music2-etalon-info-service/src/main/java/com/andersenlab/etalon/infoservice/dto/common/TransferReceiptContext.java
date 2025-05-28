package com.andersenlab.etalon.infoservice.dto.common;

import com.andersenlab.etalon.infoservice.util.enums.CurrencyName;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransferReceiptContext(
    String receiptNumber,
    String senderFullName,
    String beneficiaryFullName,
    String senderAccountNumber,
    String beneficiaryAccountNumber,
    ZonedDateTime transferDate,
    String beneficiaryBank,
    String description,
    BigDecimal totalAmount,
    CurrencyName currency) {}
