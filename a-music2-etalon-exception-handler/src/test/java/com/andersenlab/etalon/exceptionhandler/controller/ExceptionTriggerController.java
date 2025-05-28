package com.andersenlab.etalon.exceptionhandler.controller;

import com.andersenlab.etalon.exceptionhandler.dto.request.ExceptionTriggerRequest;
import com.andersenlab.etalon.exceptionhandler.dto.request.ValidationDto;
import com.andersenlab.etalon.exceptionhandler.exception.EnumConversionException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.validation.Valid;
import java.util.Collections;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ExceptionTriggerController.API_PREFIX)
public class ExceptionTriggerController {

  public static final String API_PREFIX = "/test";
  public static final String CUSTOM_EXCEPTION_TRIGGER_PATH = "/custom-exception";
  public static final String INVALID_FORMAT_EXCEPTION_TRIGGER_PATH = "/invalid-format-exception";
  public static final String FEIGN_EXCEPTION_TRIGGER_PATH = "/feign-exception";
  public static final String VALIDATION_EXCEPTION_TRIGGER_PATH = "/validation-exception";
  public static final String ENUM_CONVERSION_EXCEPTION_TRIGGER_PATH = "/enum-conversion-exception";

  @PostMapping(CUSTOM_EXCEPTION_TRIGGER_PATH)
  public void throwCustomException(@RequestBody ExceptionTriggerRequest request) {
    throw request
        .exceptionType()
        .toException(request.message(), request.exceptionType().getStatus());
  }

  @PostMapping(FEIGN_EXCEPTION_TRIGGER_PATH)
  public void throwFeign() {
    Response fakeResp =
        Response.builder()
            .status(502)
            .reason("Bad Gateway")
            .request(
                Request.create(
                    Request.HttpMethod.GET, "/", Collections.emptyMap(), null, null, null))
            .build();
    throw FeignException.errorStatus("triggerFeign", fakeResp);
  }

  @PostMapping(INVALID_FORMAT_EXCEPTION_TRIGGER_PATH)
  public void throwInvalidFormatException() throws InvalidFormatException {
    throw InvalidFormatException.from(null, "message", "value", String.class);
  }

  @PostMapping(VALIDATION_EXCEPTION_TRIGGER_PATH)
  public void throwValidationException(@Valid @RequestBody ValidationDto ignoredValidationDto) {}

  @PostMapping(ENUM_CONVERSION_EXCEPTION_TRIGGER_PATH)
  public void throwEnumConversionException() {
    throw new EnumConversionException("enum conversion failed");
  }
}
