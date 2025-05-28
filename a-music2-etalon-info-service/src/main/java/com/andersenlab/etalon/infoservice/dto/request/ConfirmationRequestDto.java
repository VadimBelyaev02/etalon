package com.andersenlab.etalon.infoservice.dto.request;

import static com.andersenlab.etalon.infoservice.util.Constants.CONFIRMATION_CODE_IS_NULL;
import static com.andersenlab.etalon.infoservice.util.Constants.CONFIRMATION_CODE_PATTERN;
import static com.andersenlab.etalon.infoservice.util.Constants.WRONG_CONFIRMATION_CODE_MESSAGE;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record ConfirmationRequestDto(
    @Schema(example = "123456")
        @NotNull(message = CONFIRMATION_CODE_IS_NULL)
        @Pattern(regexp = CONFIRMATION_CODE_PATTERN, message = WRONG_CONFIRMATION_CODE_MESSAGE)
        String confirmationCode) {}
