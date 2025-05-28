package com.andersenlab.etalon.loanservice.dto.account.request;

import lombok.Builder;

@Builder
public record AccountCreationRequestDto(String userId, String type) {}
