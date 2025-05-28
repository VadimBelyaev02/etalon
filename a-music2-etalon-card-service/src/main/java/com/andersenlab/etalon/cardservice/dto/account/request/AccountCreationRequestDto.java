package com.andersenlab.etalon.cardservice.dto.account.request;

import com.andersenlab.etalon.cardservice.util.enums.Currency;
import lombok.Builder;

@Builder(toBuilder = true)
public record AccountCreationRequestDto(String userId, String type, Currency currency) {}
