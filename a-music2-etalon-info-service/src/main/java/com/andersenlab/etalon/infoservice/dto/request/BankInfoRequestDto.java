package com.andersenlab.etalon.infoservice.dto.request;

import static com.andersenlab.etalon.infoservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.infoservice.util.Constants.CARD_NUMBER_PATTERN;
import static com.andersenlab.etalon.infoservice.util.Constants.INVALID_ACCOUNT_NUMBER_MESSAGE;
import static com.andersenlab.etalon.infoservice.util.Constants.INVALID_CARD_NUMBER_MESSAGE;

import jakarta.validation.constraints.Pattern;

public record BankInfoRequestDto(
    @Pattern(regexp = CARD_NUMBER_PATTERN, message = INVALID_CARD_NUMBER_MESSAGE) String cardNumber,
    @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = INVALID_ACCOUNT_NUMBER_MESSAGE)
        String iban) {}
