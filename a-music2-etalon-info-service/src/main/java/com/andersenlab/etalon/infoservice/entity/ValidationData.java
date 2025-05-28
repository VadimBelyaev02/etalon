package com.andersenlab.etalon.infoservice.entity;

import lombok.Builder;

@Builder(toBuilder = true)
public record ValidationData(ConfirmationEntity confirmationEntity, int confirmationCode) {}
