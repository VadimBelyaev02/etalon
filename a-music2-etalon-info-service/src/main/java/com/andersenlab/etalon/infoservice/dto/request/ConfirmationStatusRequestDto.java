package com.andersenlab.etalon.infoservice.dto.request;

import lombok.Builder;

@Builder
public record ConfirmationStatusRequestDto(Long targetId, boolean isRegistration) {}
