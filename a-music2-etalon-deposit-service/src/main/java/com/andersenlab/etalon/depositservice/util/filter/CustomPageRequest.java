package com.andersenlab.etalon.depositservice.util.filter;

import com.andersenlab.etalon.depositservice.entity.DepositEntity;
import com.andersenlab.etalon.depositservice.util.Constants;
import com.andersenlab.etalon.depositservice.validator.ValidFieldName;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springdoc.core.annotations.ParameterObject;

@Builder(toBuilder = true)
@ParameterObject
public record CustomPageRequest(
    @Parameter(name = "pageNo")
        @Schema(example = "0")
        @Min(value = 0, message = "Page number must be 0 or greater")
        Integer pageNo,
    @Parameter(name = "pageSize")
        @Schema(example = "10")
        @Min(value = 1, message = "Page size must be 1 or greater")
        Integer pageSize,
    @Parameter(name = "sortBy")
        @Schema(example = "id")
        @ValidFieldName(targetClass = DepositEntity.class)
        String sortBy,
    @Parameter(name = "orderBy")
        @Schema(example = "asc")
        @Pattern(regexp = Constants.ORDER_BY_PATTERN, message = "Order by must be 'asc' or 'desc'")
        String orderBy) {}
