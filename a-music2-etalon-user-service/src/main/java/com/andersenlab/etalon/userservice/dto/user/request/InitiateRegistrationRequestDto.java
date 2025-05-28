package com.andersenlab.etalon.userservice.dto.user.request;

import static com.andersenlab.etalon.userservice.util.Constants.BANKS_PRIVACY_POLICY_MUST_BE_ACCEPTED;
import static com.andersenlab.etalon.userservice.util.Constants.PESEL_MUST_CONTAIN_11_DIGITS;
import static com.andersenlab.etalon.userservice.util.Constants.PESEL_MUST_CONTAIN_ONLY_DIGITS;
import static com.andersenlab.etalon.userservice.util.Constants.PESEL_MUST_NOT_BE_BLANK;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record InitiateRegistrationRequestDto(
    @NotBlank(message = PESEL_MUST_NOT_BE_BLANK)
        @Size(min = 11, max = 11, message = PESEL_MUST_CONTAIN_11_DIGITS)
        @Pattern(regexp = "\\d{11}", message = PESEL_MUST_CONTAIN_ONLY_DIGITS)
        String pesel,
    @AssertTrue(message = BANKS_PRIVACY_POLICY_MUST_BE_ACCEPTED) boolean isPrivacyPolicyAccepted) {}
