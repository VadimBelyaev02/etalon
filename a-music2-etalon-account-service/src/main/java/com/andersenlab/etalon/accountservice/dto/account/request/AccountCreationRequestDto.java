package com.andersenlab.etalon.accountservice.dto.account.request;

import com.andersenlab.etalon.accountservice.util.enums.Currency;
import lombok.Builder;

@Builder
public record AccountCreationRequestDto(String userId, String type, Currency currency) {}
