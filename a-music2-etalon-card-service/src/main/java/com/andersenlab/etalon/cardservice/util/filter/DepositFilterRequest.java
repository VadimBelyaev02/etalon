package com.andersenlab.etalon.cardservice.util.filter;

import com.andersenlab.etalon.cardservice.dto.deposit.DepositStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.springdoc.core.annotations.ParameterObject;

@Builder(toBuilder = true)
@ParameterObject
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DepositFilterRequest(
    @Parameter(name = "accountNumber") @Schema(example = "PL04234567840000000000000001")
        String accountNumber,
    @Parameter(name = "statusList") @Schema(anyOf = DepositStatus.class)
        List<DepositStatus> statusList) {}
