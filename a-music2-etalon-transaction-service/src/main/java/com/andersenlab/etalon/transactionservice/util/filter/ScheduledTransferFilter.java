package com.andersenlab.etalon.transactionservice.util.filter;

import com.andersenlab.etalon.transactionservice.util.enums.ScheduleStatus;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
public record ScheduledTransferFilter(
    @Parameter(name = "status", description = "Filter by schedule status: ACTIVE or DEACTIVATED")
        ScheduleStatus status) {}
