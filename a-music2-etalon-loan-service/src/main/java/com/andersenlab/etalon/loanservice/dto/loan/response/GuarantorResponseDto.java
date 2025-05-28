package com.andersenlab.etalon.loanservice.dto.loan.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GuarantorResponseDto(
    @Schema(example = "1") Long id,
    @Schema(example = "12345678901") String pesel,
    @Schema(example = "Tadeusz") String firstName,
    @Schema(example = "Nowak") String lastName) {}
