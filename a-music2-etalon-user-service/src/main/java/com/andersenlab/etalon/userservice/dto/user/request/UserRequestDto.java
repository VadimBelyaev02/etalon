package com.andersenlab.etalon.userservice.dto.user.request;

import static com.andersenlab.etalon.userservice.util.Constants.EMAIL_PATTERN;
import static com.andersenlab.etalon.userservice.util.Constants.WRONG_EMAIL_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(toBuilder = true)
public record UserRequestDto(
    String id,
    @NotBlank(message = "First name cannot be blank or null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in first name")
        @Schema(example = "Grzegorz")
        String firstName,
    @NotBlank(message = "Second name cannot be null")
        @Pattern(
            regexp = "^(?![- ])(?!.*[- ]$)[a-zA-Z\\s-]{2,30}$",
            message = "Invalid characters in second name")
        @Schema(example = "Bzeczyszczykiewicz")
        String lastName,
    @NotBlank(message = "Email cannot be blank or null")
        @Pattern(regexp = EMAIL_PATTERN, message = WRONG_EMAIL_MESSAGE)
        @Schema(example = "username@domain.com")
        String email,
    @NotBlank(message = "PESEL cannot be blank or null")
        @Pattern(regexp = "^\\d{11}$", message = "Invalid format. PESEL must include 11 digits.")
        @Schema(example = "81100216357")
        String pesel,
    @Valid AddressDto address,
    @Valid EmploymentDataDto employmentData) {}
