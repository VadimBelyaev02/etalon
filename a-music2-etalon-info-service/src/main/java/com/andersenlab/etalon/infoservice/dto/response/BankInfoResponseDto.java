package com.andersenlab.etalon.infoservice.dto.response;

import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record BankInfoResponseDto(
    boolean isForeignBank,
    String fullName,
    String callName,
    List<BankDetailResponseDto> bankDetails) {}
