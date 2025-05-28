package com.andersenlab.etalon.cardservice.dto.card.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record ShortCardInfoDto(Long id, String cardProductName, String maskedCardNumber) {}
