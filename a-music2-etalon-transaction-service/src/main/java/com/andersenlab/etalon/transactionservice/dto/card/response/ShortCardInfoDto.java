package com.andersenlab.etalon.transactionservice.dto.card.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ShortCardInfoDto(Long id, String cardProductName, String maskedCardNumber) {}
