package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;

public record CreateConfirmationResponseDto(
    Long confirmationId, ConfirmationMethod confirmationMethod) {}
