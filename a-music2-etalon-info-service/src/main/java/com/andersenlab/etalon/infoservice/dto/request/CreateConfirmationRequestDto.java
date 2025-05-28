package com.andersenlab.etalon.infoservice.dto.request;

import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import jakarta.validation.constraints.NotNull;

public record CreateConfirmationRequestDto(
    Long targetId,
    @NotNull(message = "Operation can not be null") Operation operation,
    @NotNull(message = "Confirmation method can not be null")
        ConfirmationMethod confirmationMethod) {}
