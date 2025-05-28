package com.andersenlab.etalon.depositservice.util.filter;

import com.andersenlab.etalon.depositservice.util.Constants;
import com.andersenlab.etalon.depositservice.util.enums.DepositStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Builder;
import org.springdoc.core.annotations.ParameterObject;

@Builder(toBuilder = true)
@ParameterObject
public record DepositFilterRequest(
    @Parameter(name = "accountNumber")
        @Schema(example = "PL04234567840000000000000001")
        @Pattern(
            regexp = Constants.ACCOUNT_NUMBER_PATTERN,
            message = Constants.WRONG_ACCOUNT_NUMBER_MESSAGE)
        String accountNumber,
    @Parameter(name = "statusList") @Schema(anyOf = DepositStatus.class)
        List<DepositStatus> statusList) {}
