package com.andersenlab.etalon.transactionservice.dto.card.request;

import com.andersenlab.etalon.transactionservice.util.enums.CardStatus;
import com.andersenlab.etalon.transactionservice.util.enums.Issuer;
import com.andersenlab.etalon.transactionservice.util.enums.ProductType;

public record RequestFilterDto(
    Issuer issuer, ProductType productType, CardStatus status, String accountNumber) {}
