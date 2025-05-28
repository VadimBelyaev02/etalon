package com.andersenlab.etalon.exceptionhandler.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  VALIDATION_FAILED("Validation failed for fields below"),
  INTERNAL_SERVER_ERROR("Internal server error"),
  EXTERNAL_SERVICE_ERROR("Error calling external service"),
  UNKNOWN_ERROR("Unknown error"),
  BUSINESS_EXCEPTION("Business exception"),
  SECURITY_EXCEPTION("Security exception"),
  TECHNICAL_EXCEPTION("Technical exception"),
  SYSTEM_EXCEPTION("System exception"),
  INVALID_FORMAT("Invalid format"),
  METHOD_ARGUMENT_INVALID("Method argument is invalid"),
  ENUM_CONVERSION("Enum conversion"),
  CONFLICT("Conflict state"),
  NOT_FOUND("Not found");

  private final String description;
}
