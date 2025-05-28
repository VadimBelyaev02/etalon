package com.andersenlab.etalon.infoservice.dto.response;

import java.io.Serializable;
import java.util.List;

public record BankContactsResponseDto(
    Long id,
    String bankName,
    String address,
    String nip,
    String regon,
    String swiftCode,
    String email,
    String phoneNumber,
    List<BankOperationModeResponseDto> operationModes)
    implements Serializable {}
