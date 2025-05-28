package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.Currency;
import lombok.Builder;

@Builder(toBuilder = true)
public record BankDetailResponseDto(String bin, String bankCode, Currency currency) {}
