package com.andersenlab.etalon.exceptionhandler.exception.handler;

import static com.andersenlab.etalon.exceptionhandler.util.Constants.TRACE_ID;

import com.andersenlab.etalon.exceptionhandler.dto.common.response.ErrorResponseDto;
import com.andersenlab.etalon.exceptionhandler.dto.common.response.ViolationDto;
import com.andersenlab.etalon.exceptionhandler.exception.BusinessException;
import com.andersenlab.etalon.exceptionhandler.exception.ConflictException;
import com.andersenlab.etalon.exceptionhandler.exception.EnumConversionException;
import com.andersenlab.etalon.exceptionhandler.exception.FeignClientException;
import com.andersenlab.etalon.exceptionhandler.exception.NotFoundException;
import com.andersenlab.etalon.exceptionhandler.exception.TechnicalException;
import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @Value("${spring.application.name}")
  private String defaultServiceIdentifier;

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponseDto> handleBusinessException(final BusinessException ex) {
    log.warn("{handleBusinessException} -> {}", ex.getMessage());
    return buildResponseEntity(ex.getErrorCode(), HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponseDto> handleConflictException(final ConflictException ex) {
    log.warn("{handleConflictException} -> {}", ex.getMessage());
    return buildResponseEntity(ex.getErrorCode(), HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleNotFoundException(final NotFoundException ex) {
    log.warn("{handleNotFoundException} -> {}", ex.getMessage());
    return buildResponseEntity(ex.getErrorCode(), HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(TechnicalException.class)
  public ResponseEntity<ErrorResponseDto> handleTechnicalException(final TechnicalException ex) {
    log.warn("{handleTechnicalException} -> {}", ex.getMessage());
    return buildResponseEntity(ex.getErrorCode(), HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<ErrorResponseDto> handleFeignClientException(
      final FeignClientException ex) {
    log.warn("{handleFeignClientException} -> {}", ex.getMessage());
    return buildResponseEntity(
        ex.getErrorCode(), ex.getStatus(), ex.getMessage(), ex.getServiceIdentifier());
  }

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<ErrorResponseDto> handleFeignException(final FeignException ex) {
    log.warn("{handleFeignException} -> {}", ex.getMessage());
    HttpStatus httpStatus = HttpStatus.resolve(ex.status());
    httpStatus = Objects.isNull(httpStatus) ? HttpStatus.INTERNAL_SERVER_ERROR : httpStatus;
    return buildResponseEntity(ErrorCode.EXTERNAL_SERVICE_ERROR, httpStatus, ex.getMessage());
  }

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidFormatException(
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
    String errorMessage =
        "Passed value [%s] is invalid for fields %s. Possible values are: %s"
            .formatted(ex.getValue(), fields, values);

    return buildResponseEntity(ErrorCode.INVALID_FORMAT, HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
      final MethodArgumentNotValidException ex) {

    Optional<FieldError> fieldError =
        Optional.ofNullable(ex)
            .map(MethodArgumentNotValidException::getBindingResult)
            .map(Errors::getFieldError);

    String message = fieldError.map(FieldError::getDefaultMessage).orElse(StringUtils.EMPTY);
    String field = fieldError.map(FieldError::getField).orElse(StringUtils.EMPTY);

    log.warn("{handleMethodArgumentNotValidException} -> message = {}, field = {}", message, field);
    List<ViolationDto> violations = null;
    if (Objects.nonNull(ex)) {
      violations =
          ex.getBindingResult().getFieldErrors().stream()
              .map(
                  fieldErrorElement ->
                      ViolationDto.builder()
                          .field(fieldErrorElement.getField())
                          .violation(fieldErrorElement.getDefaultMessage())
                          .build())
              .toList();
    }
    return buildResponseEntity(
        ErrorCode.METHOD_ARGUMENT_INVALID, HttpStatus.BAD_REQUEST, message, violations);
  }

  @ExceptionHandler(EnumConversionException.class)
  public ResponseEntity<ErrorResponseDto> handleEnumConversionException(
      final EnumConversionException ex) {
    return buildResponseEntity(ErrorCode.ENUM_CONVERSION, HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  private ResponseEntity<ErrorResponseDto> buildResponseEntity(
      ErrorCode code, HttpStatus status, String message) {
    return buildResponseEntity(code, status, message, List.of(), defaultServiceIdentifier);
  }

  private ResponseEntity<ErrorResponseDto> buildResponseEntity(
      ErrorCode code, HttpStatus status, String message, String serviceIdentifier) {
    return buildResponseEntity(code, status, message, List.of(), serviceIdentifier);
  }

  private ResponseEntity<ErrorResponseDto> buildResponseEntity(
      ErrorCode code, HttpStatus status, String message, List<ViolationDto> violations) {
    return buildResponseEntity(code, status, message, violations, defaultServiceIdentifier);
  }

  private ResponseEntity<ErrorResponseDto> buildResponseEntity(
      ErrorCode code,
      HttpStatus status,
      String message,
      List<ViolationDto> violations,
      String serviceIdentifier) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    ErrorResponseDto body =
        ErrorResponseDto.builder()
            .timestamp(LocalDateTime.now())
            .traceId(currentTraceId())
            .errorCode(code.name())
            .message(message)
            .serviceIdentifier(serviceIdentifier)
            .violations(violations)
            .description(code.getDescription())
            .build();

    return ResponseEntity.status(status).headers(headers).body(body);
  }

  private String currentTraceId() {
    return MDC.get(TRACE_ID);
  }
}
