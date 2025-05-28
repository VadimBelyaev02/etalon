package com.andersenlab.etalon.userservice.dto.user.request;

import com.andersenlab.etalon.userservice.util.validation.AlphaPunctuation;
import com.andersenlab.etalon.userservice.util.validation.AlphaPunctuationNumeric;
import com.andersenlab.etalon.userservice.util.validation.AlphaPunctuationNumericStartUpper;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressDto(
    @Schema(example = "Opole")
        @AlphaPunctuation(message = "Invalid voivodeship")
        @Size(min = 2, max = 20)
        String voivodeship,
    @Schema(example = "Prudnik")
        @AlphaPunctuation(message = "Invalid city")
        @Size(min = 2, max = 40)
        String city,
    @Schema(example = "Kuberskiego")
        @AlphaPunctuationNumericStartUpper(message = "Invalid street")
        @Size(min = 3, max = 100)
        String street,
    @Schema(example = "48-2")
        @AlphaPunctuationNumeric(message = "Invalid building")
        @Size(min = 1, max = 10)
        String building,
    @Schema(example = "162E")
        @AlphaPunctuationNumeric(message = "Invalid apartment")
        @Size(min = 1, max = 10)
        String apartment,
    @Schema(example = "15-631") @Pattern(regexp = "^\\d{2}-\\d{3}$", message = "Invalid post code")
        String postCode) {}
