package com.andersenlab.etalon.depositservice.dto.deposit.request;

import com.andersenlab.etalon.depositservice.util.Constants;
import com.andersenlab.etalon.depositservice.validator.AtLeastOneFieldNotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder(toBuilder = true)
@AtLeastOneFieldNotEmpty(fields = {"interestAccountNumber", "finalTransferAccountNumber"})
public record DepositUpdateRequestDto(
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(
            regexp = Constants.ACCOUNT_NUMBER_PATTERN,
            message = Constants.WRONG_ACCOUNT_NUMBER_MESSAGE)
        String interestAccountNumber,
    @Schema(example = "PL04234567840000000000000001")
        @Pattern(
            regexp = Constants.ACCOUNT_NUMBER_PATTERN,
            message = Constants.WRONG_ACCOUNT_NUMBER_MESSAGE)
        String finalTransferAccountNumber) {}
