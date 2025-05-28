package com.andersenlab.etalon.userservice.dto.info.request;

import lombok.Builder;

@Builder
public record ConfirmationStatusRequestDto(Long targetId, boolean isRegistration) {}
