package com.andersenlab.etalon.transactionservice.dto.sqs;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;

@Builder(toBuilder = true)
public record TransactionQueueMessage(
    long transactionId, BigDecimal loanInterestAmount, BigDecimal loanPenaltyAmount)
    implements Serializable {}
