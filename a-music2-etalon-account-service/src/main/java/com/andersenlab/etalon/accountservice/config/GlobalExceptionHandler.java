// package com.andersenlab.etalon.accountservice.config;
//
// import com.andersenlab.etalon.accountservice.dto.common.response.MessageResponseDto;
// import com.andersenlab.etalon.accountservice.dto.common.response.ValidationExceptionResponseDto;
// import com.andersenlab.etalon.accountservice.exception.BusinessExceptionQ;
// import com.fasterxml.jackson.databind.JsonMappingException;
// import com.fasterxml.jackson.databind.exc.InvalidFormatException;
// import java.lang.reflect.Field;
// import java.util.Arrays;
// import java.util.Objects;
// import java.util.Optional;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.Errors;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;
//
// @Slf4j
// @ControllerAdvice
// public class GlobalExceptionHandler {
//  @ExceptionHandler(BusinessExceptionQ.class)
//  public ResponseEntity<MessageResponseDto> handleBusinessException(final BusinessExceptionQ ex) {
//    log.warn("{handleBusinessException} -> {}", ex.getMessage());
//
//    return ResponseEntity.status(ex.getHttpStatus()).body(new
// MessageResponseDto(ex.getMessage()));
//  }
//
//  @ExceptionHandler(InvalidFormatException.class)
//  public ResponseEntity<MessageResponseDto> handleInvalidFormatException(
//      final InvalidFormatException ex) {
//    log.warn("{handleInvalidFormatException} -> {}", ex.getMessage());
//
//    String fields =
//        ex.getPath().stream()
//            .filter(Objects::nonNull)
//            .map(JsonMappingException.Reference::getFieldName)
//            .toList()
//            .toString();
//
//    String values =
//        Arrays.stream(ex.getTargetType().getFields())
//            .filter(Objects::nonNull)
//            .map(Field::getName)
//            .toList()
//            .toString();
//
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//        .body(
//            new MessageResponseDto(
//                "Passed value [%s] is invalid for fields %s. Possible values are: %s"
//                    .formatted(ex.getValue(), fields, values)));
//  }
//
//  @ExceptionHandler(MethodArgumentNotValidException.class)
//  public ResponseEntity<ValidationExceptionResponseDto> handleMethodArgumentNotValidException(
//      final MethodArgumentNotValidException ex) {
//
//    Optional<FieldError> fieldError =
//        Optional.ofNullable(ex)
//            .map(MethodArgumentNotValidException::getBindingResult)
//            .map(Errors::getFieldError);
//
//    String message = fieldError.map(FieldError::getDefaultMessage).orElse(StringUtils.EMPTY);
//
//    String field = fieldError.map(FieldError::getField).orElse(StringUtils.EMPTY);
//
//    log.warn("{handleMethodArgumentNotValidException} -> message = {}, field = {}", message,
// field);
//
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//        .body(new ValidationExceptionResponseDto(message, field));
//  }
// }
