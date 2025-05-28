package com.andersenlab.etalon.userservice.dto.user.request;

import com.andersenlab.etalon.userservice.util.validation.PasswordMatches;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@PasswordMatches
public record CompleteRegistrationRequestDto(
    @NotNull(message = "Password is required")
        @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
        @Pattern(
            regexp =
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@!$?*()\\[\\]{}'\";:\\\\/<>,_.-])[A-Za-z\\d@!$?*()\\[\\]{}'\";:\\\\/<>,_.-]{8,50}$",
            message =
                "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
        String password,
    @NotNull(message = "Repeated password is required") String repeatedPassword) {}
