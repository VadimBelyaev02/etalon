package com.andersenlab.etalon.userservice.dto.modification.confirmations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ConfirmEmailModifyRequestDto(
    @NotNull long modificationId,
    @NotNull @Pattern(regexp = "\\d{4}", message = "The number must be exactly 4 digits long.")
        String confirmationCode) {}
