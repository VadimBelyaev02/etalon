package com.andersenlab.etalon.exceptionhandler.dto.request;

import com.andersenlab.etalon.exceptionhandler.dto.common.enums.ExceptionType;
import com.andersenlab.etalon.exceptionhandler.exception.GenericException;
import lombok.Builder;

@Builder(toBuilder = true)
public record ExceptionTriggerRequest(
    ExceptionType exceptionType,
    String message,
    Class<? extends GenericException> exceptionClass) {}
