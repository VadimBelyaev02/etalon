package com.andersenlab.etalon.infoservice.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record UserResponseDto(
    String id, String email, String firstName, String lastName, String pesel) {}
