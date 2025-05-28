package com.andersenlab.etalon.accountservice.dto.account.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AccountNumberResponseDto(
    @Schema(example = "PL**********************0172") String accountNumber) {}
