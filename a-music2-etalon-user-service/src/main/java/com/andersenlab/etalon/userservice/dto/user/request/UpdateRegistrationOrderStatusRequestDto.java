package com.andersenlab.etalon.userservice.dto.user.request;

import com.andersenlab.etalon.userservice.util.OrderStatus;
import lombok.Builder;

@Builder(toBuilder = true)
public record UpdateRegistrationOrderStatusRequestDto(
    String registrationOrderId, OrderStatus orderStatus) {}
