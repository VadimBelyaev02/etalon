package com.andersenlab.etalon.depositservice.config;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.common.response.ValidationExceptionResponseDto;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<MessageResponseDto> handleBusinessException(final BusinessException ex) {
    log.warn("{handleBusinessException} -> {}", ex.getMessage());

    return ResponseEntity.status(ex.getHttpStatus()).body(new MessageResponseDto(ex.getMessage()));
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<String> handleFeignException(final FeignException ex) {
    log.warn("{handleFeignException} -> {}", ex.getMessage());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return ResponseEntity.status(HttpStatus.valueOf(ex.status()))
        .headers(headers)
        .body(ex.contentUTF8());
  }

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<MessageResponseDto> handleInvalidFormatException(
      final InvalidFormatException ex) {
    log.warn("{handleInvalidFormatException} -> {}", ex.getMessage());

    String fields =
        ex.getPath().stream()
            .filter(Objects::nonNull)
            .map(JsonMappingException.Reference::getFieldName)
            .toList()
            .toString();

    String values =
        Arrays.stream(ex.getTargetType().getFields())
            .filter(Objects::nonNull)
            .map(Field::getName)
            .toList()
            .toString();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new MessageResponseDto(
                "Passed value [%s] is invalid for fields %s. Possible values are: %s"
                    .formatted(ex.getValue(), fields, values)));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationExceptionResponseDto> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex) {
    if (hasNoFieldErrors(ex)) {
      return handleNoFieldErrors(ex);
    }
    return handleFieldErrors(ex);
  }

  private boolean hasNoFieldErrors(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors().isEmpty();
  }

  private ResponseEntity<ValidationExceptionResponseDto> handleNoFieldErrors(
      MethodArgumentNotValidException ex) {
    String object = collectErrorDetails(ex, ObjectError::getObjectName, "Unknown object");
    String message = collectErrorDetails(ex, ObjectError::getDefaultMessage, "Unknown message");
    logValidationWarning(message, object);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationExceptionResponseDto(message, object));
  }

  private ResponseEntity<ValidationExceptionResponseDto> handleFieldErrors(
      MethodArgumentNotValidException ex) {
    String fields = collectFieldErrorDetails(ex, FieldError::getField, "Unknown field");
    String messages =
        collectFieldErrorDetails(ex, FieldError::getDefaultMessage, "Unknown message");
    logValidationWarning(messages, fields);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationExceptionResponseDto(messages, fields));
  }

  private String collectErrorDetails(
      MethodArgumentNotValidException ex,
      Function<ObjectError, String> mapper,
      String defaultValue) {
    String result =
        ex.getBindingResult().getAllErrors().stream()
            .map(mapper)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.joining(", "));
    return result.isEmpty() ? defaultValue : result;
  }

  private String collectFieldErrorDetails(
      MethodArgumentNotValidException ex,
      Function<FieldError, String> mapper,
      String defaultValue) {
    String result =
        ex.getBindingResult().getFieldErrors().stream()
            .map(mapper)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.joining(", "));
    return result.isEmpty() ? defaultValue : result;
  }

  private void logValidationWarning(String message, String details) {
    log.warn(
        "{handleMethodArgumentNotValidException} -> message = {}, details = {}", message, details);
  }
}
