package com.andersenlab.etalon.transactionservice.dto.transaction.response;

import com.andersenlab.etalon.transactionservice.util.enums.CurrencyName;
import com.andersenlab.etalon.transactionservice.util.enums.ScheduleFrequency;
import com.andersenlab.etalon.transactionservice.util.enums.ScheduleStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ScheduledTransferDto(
    Long id,
    String name,
    ScheduleStatus status,
    ScheduleFrequency frequency,
    BigDecimal amount,
    CurrencyName currency,
    String senderAccountNumber,
    LocalDate startDate,
    LocalDate endDate,
    LocalDate nextTransferDate) {}
