package com.andersenlab.etalon.infoservice.dto.response;

import com.andersenlab.etalon.infoservice.util.enums.BankType;
import java.io.Serializable;
import java.util.List;

public record BankBranchesAndAtmsResponseDto(
    Long id,
    BankType type,
    String name,
    String city,
    String address,
    String latitude,
    String longitude,
    List<BankOperationModeResponseDto> operationModes)
    implements Serializable {}
