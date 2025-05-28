package com.andersenlab.etalon.userservice.dto.info.request;

import com.andersenlab.etalon.userservice.util.ConfirmationMethod;
import com.andersenlab.etalon.userservice.util.Operation;
import lombok.Builder;

@Builder
public record CreateConfirmationRequestDto(
    Long targetId,
    Operation operation,
    ConfirmationMethod confirmationMethod,
    boolean isRegistration) {}
