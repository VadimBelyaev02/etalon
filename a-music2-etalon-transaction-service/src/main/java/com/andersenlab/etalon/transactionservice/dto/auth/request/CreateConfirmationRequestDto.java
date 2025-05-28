package com.andersenlab.etalon.transactionservice.dto.auth.request;

import com.andersenlab.etalon.transactionservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.transactionservice.util.enums.Operation;

public record CreateConfirmationRequestDto(
    Long targetId, Operation operation, ConfirmationMethod confirmationMethod) {}
