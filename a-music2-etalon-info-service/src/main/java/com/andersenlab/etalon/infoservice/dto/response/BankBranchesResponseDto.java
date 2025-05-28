package com.andersenlab.etalon.infoservice.dto.response;

import lombok.Builder;

@Builder
public record BankBranchesResponseDto(Long id, String name, String city, String address) {}
