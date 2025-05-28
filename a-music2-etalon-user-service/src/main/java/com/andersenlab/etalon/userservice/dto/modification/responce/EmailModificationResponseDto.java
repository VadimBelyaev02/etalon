package com.andersenlab.etalon.userservice.dto.modification.responce;

import lombok.Builder;

@Builder(toBuilder = true)
public record EmailModificationResponseDto(long confirmationId) {}
