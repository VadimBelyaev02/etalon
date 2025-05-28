package com.andersenlab.etalon.userservice.dto.user.response;

import com.andersenlab.etalon.userservice.util.ConfirmationMethod;
import com.andersenlab.etalon.userservice.util.OrderStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record InitiateRegistrationResponseDto(
    Long confirmationId,
    ConfirmationMethod confirmationMethod,
    String maskedEmail,
    OrderStatus orderStatus) {}
