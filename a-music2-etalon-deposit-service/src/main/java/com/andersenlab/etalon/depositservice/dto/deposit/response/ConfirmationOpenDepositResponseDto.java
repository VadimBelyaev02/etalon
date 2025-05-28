package com.andersenlab.etalon.depositservice.dto.deposit.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConfirmationOpenDepositResponseDto(@Schema(example = "1") Long confirmationId) {}
