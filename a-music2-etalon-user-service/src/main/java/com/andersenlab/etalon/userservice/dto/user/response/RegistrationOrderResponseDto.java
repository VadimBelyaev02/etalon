package com.andersenlab.etalon.userservice.dto.user.response;

import com.andersenlab.etalon.userservice.util.OrderStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record RegistrationOrderResponseDto(
    String pesel, OrderStatus orderStatus, String registrationId) {}
