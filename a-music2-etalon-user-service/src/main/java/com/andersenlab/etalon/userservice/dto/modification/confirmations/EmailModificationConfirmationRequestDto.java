package com.andersenlab.etalon.userservice.dto.modification.confirmations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmailModificationConfirmationRequestDto(
    @NotNull long modificationId,
    @NotNull @Pattern(regexp = "\\d{6}", message = "The number must be exactly 6 digits long.")
        String confirmationCode) {}
