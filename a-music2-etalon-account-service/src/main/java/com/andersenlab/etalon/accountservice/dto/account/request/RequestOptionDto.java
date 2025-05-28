package com.andersenlab.etalon.accountservice.dto.account.request;

import static com.andersenlab.etalon.accountservice.util.Constants.ACCOUNT_NUMBER_PATTERN;
import static com.andersenlab.etalon.accountservice.util.Constants.CARD_NUMBER_PATTERN;
import static com.andersenlab.etalon.accountservice.util.Constants.PHONE_NUMBER_PATTERN;
import static com.andersenlab.etalon.accountservice.util.Constants.WRONG_ACCOUNT_NUMBER_MESSAGE;
import static com.andersenlab.etalon.accountservice.util.Constants.WRONG_CARD_NUMBER_MESSAGE;
import static com.andersenlab.etalon.accountservice.util.Constants.WRONG_PHONE_NUMBER_MESSAGE;

import jakarta.validation.constraints.Pattern;

public record RequestOptionDto(
    @Pattern(regexp = PHONE_NUMBER_PATTERN, message = WRONG_PHONE_NUMBER_MESSAGE)
        String phoneNumber,
    @Pattern(regexp = ACCOUNT_NUMBER_PATTERN, message = WRONG_ACCOUNT_NUMBER_MESSAGE)
        String accountNumber,
    @Pattern(regexp = CARD_NUMBER_PATTERN, message = WRONG_CARD_NUMBER_MESSAGE)
        String cardNumber) {}
