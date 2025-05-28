package com.andersenlab.etalon.exceptionhandler.dto.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ViolationDto(String field, String violation) {}
