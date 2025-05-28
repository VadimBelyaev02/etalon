package com.andersenlab.etalon.infoservice.dto.response;

public record EmailModificationInfoResponseDto(
    String userId, String newEmail, String confirmationCode) {}
