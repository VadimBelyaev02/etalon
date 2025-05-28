package com.andersenlab.etalon.cardservice.dto.user.request;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserRequestDto(String userId, String firstName, String lastName) {}
