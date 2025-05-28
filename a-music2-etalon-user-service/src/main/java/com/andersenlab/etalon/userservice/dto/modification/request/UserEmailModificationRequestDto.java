package com.andersenlab.etalon.userservice.dto.modification.request;

import static com.andersenlab.etalon.userservice.util.Constants.EMAIL_PATTERN;
import static com.andersenlab.etalon.userservice.util.Constants.WRONG_EMAIL_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserEmailModificationRequestDto(
    @NotBlank(message = "Email cannot be blank or null")
        @Pattern(regexp = EMAIL_PATTERN, message = WRONG_EMAIL_MESSAGE)
        @Schema(example = "username@domain.com")
        String email) {}
