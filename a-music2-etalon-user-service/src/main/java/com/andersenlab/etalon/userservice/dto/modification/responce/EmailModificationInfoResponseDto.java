package com.andersenlab.etalon.userservice.dto.modification.responce;

import lombok.Builder;

@Builder(toBuilder = true)
public record EmailModificationInfoResponseDto(
    String userId, String newEmail, String confirmationCode) {}
