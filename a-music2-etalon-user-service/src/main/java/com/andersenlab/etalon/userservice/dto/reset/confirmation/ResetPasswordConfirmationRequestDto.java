package com.andersenlab.etalon.userservice.dto.reset.confirmation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
public record ResetPasswordConfirmationRequestDto(
    @NotBlank(message = "Token cannot be blank or null")
        @Schema(
            example = "123e4567-e89b-12d3-a456-426614174000",
            description = "Reset token received in email")
        String token,
    @NotBlank(message = "New password cannot be blank or null")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(example = "NewSecurePassword123@", description = "New password")
        String newPassword) {}
