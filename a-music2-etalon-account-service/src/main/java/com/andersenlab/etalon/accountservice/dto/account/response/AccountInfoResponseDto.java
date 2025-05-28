package com.andersenlab.etalon.accountservice.dto.account.response;

import static com.andersenlab.etalon.accountservice.util.Constants.JSON_MASK_FULL_NAME_PATTERN;
import static com.andersenlab.etalon.accountservice.util.Constants.JSON_MASK_FULL_NAME_REPLACE_PATTERN;

import com.andersenlab.etalon.accountservice.annotations.JsonMask;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record AccountInfoResponseDto(
    @Schema(example = "Gustav K.")
        @JsonMask(
            replaceRegExp = JSON_MASK_FULL_NAME_PATTERN,
            replaceWith = JSON_MASK_FULL_NAME_REPLACE_PATTERN)
        String fullName,
    @ArraySchema(schema = @Schema(implementation = AccountCurrencyResponseDto.class))
        List<AccountCurrencyResponseDto> accounts) {}
