package com.andersenlab.etalon.infoservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConfirmationBlockedResponseDto(String message, ZonedDateTime unblockDate) {}
