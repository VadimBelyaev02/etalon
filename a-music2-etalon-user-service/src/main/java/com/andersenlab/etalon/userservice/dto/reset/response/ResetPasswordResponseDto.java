package com.andersenlab.etalon.userservice.dto.reset.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ResetPasswordResponseDto(String message) {}
