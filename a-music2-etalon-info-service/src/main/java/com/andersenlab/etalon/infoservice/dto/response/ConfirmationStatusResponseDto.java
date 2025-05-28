package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import lombok.Builder;

@Builder
public record ConfirmationStatusResponseDto(ConfirmationStatus confirmationStatus) {}
