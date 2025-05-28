package com.andersenlab.etalon.loanservice.config;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.common.response.ValidationExceptionResponseDto;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<MessageResponseDto> handleBusinessException(final ValidationException ex) {
    log.warn("{handleBusinessException} -> {}", ex.getMessage());

    return ResponseEntity.status(ex.getHttpStatus()).body(new MessageResponseDto(ex.getMessage()));
  }

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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationExceptionResponseDto> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex) {

    Optional<FieldError> fieldError =
        Optional.ofNullable(ex)
            .map(MethodArgumentNotValidException::getBindingResult)
            .map(Errors::getFieldError);

    String message = fieldError.map(FieldError::getDefaultMessage).orElse(StringUtils.EMPTY);

    String field = fieldError.map(FieldError::getField).orElse(StringUtils.EMPTY);

    log.warn("{handleMethodArgumentNotValidException} -> message = {}, field = {}", message, field);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ValidationExceptionResponseDto(message, field));
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
}
