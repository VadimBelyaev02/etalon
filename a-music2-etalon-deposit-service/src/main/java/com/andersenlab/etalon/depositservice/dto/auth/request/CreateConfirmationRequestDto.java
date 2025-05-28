package com.andersenlab.etalon.depositservice.dto.auth.request;

import com.andersenlab.etalon.depositservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.depositservice.util.enums.Operation;

public record CreateConfirmationRequestDto(
    Long targetId, Operation operation, ConfirmationMethod confirmationMethod) {}
