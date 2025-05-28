package com.andersenlab.etalon.loanservice.dto.loan.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record GuarantorRequestDto(
    @Schema(example = "89862540222")
        @NotBlank(message = "PESEL cannot be blank or null")
        @Pattern(regexp = "^\\d{11}$", message = "Invalid format. PESEL must include 11 digits.")
        String pesel,
    @Schema(example = "Jan")
        @NotBlank(message = "First name cannot be blank or null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in first name")
        String firstName,
    @Schema(example = "Kowalski")
        @NotBlank(message = "Second name cannot be null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in last name")
        String lastName) {}
