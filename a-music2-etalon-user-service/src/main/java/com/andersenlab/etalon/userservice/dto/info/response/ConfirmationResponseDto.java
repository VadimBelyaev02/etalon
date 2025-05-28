package com.andersenlab.etalon.userservice.dto.info.response;

import com.andersenlab.etalon.userservice.util.ConfirmationStatus;
import lombok.Builder;

@Builder
public record ConfirmationResponseDto(Long confirmationId, ConfirmationStatus confirmationStatus) {}
