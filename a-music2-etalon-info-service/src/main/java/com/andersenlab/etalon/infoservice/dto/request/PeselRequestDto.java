package com.andersenlab.etalon.infoservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record PeselRequestDto(
    @NotBlank(message = "PESEL cannot be blank or null")
        @Pattern(regexp = "^\\d{11}$", message = "Invalid format. PESEL must include 11 digits.")
        @Schema(example = "81100216357")
        String pesel) {}
