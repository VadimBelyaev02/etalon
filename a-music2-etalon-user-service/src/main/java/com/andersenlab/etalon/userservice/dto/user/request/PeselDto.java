package com.andersenlab.etalon.userservice.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PeselDto(
    @NotBlank(message = "PESEL cannot be blank or null")
        @Pattern(regexp = "^\\d{11}$", message = "Invalid format. PESEL number shall be 11 digits.")
        @Schema(example = "81100216357")
        String pesel) {}
