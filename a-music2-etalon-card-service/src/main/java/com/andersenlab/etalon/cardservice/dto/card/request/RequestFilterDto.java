package com.andersenlab.etalon.cardservice.dto.card.request;

import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import com.andersenlab.etalon.cardservice.util.enums.ProductType;
import lombok.Builder;

@Builder
public record RequestFilterDto(
    Issuer issuer, ProductType productType, CardStatus status, String accountNumber) {}
