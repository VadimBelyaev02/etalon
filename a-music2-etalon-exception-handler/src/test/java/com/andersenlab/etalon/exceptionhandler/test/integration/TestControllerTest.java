package com.andersenlab.etalon.exceptionhandler.test.integration;

import static com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController.API_PREFIX;
import static com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController.CUSTOM_EXCEPTION_TRIGGER_PATH;
import static com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController.ENUM_CONVERSION_EXCEPTION_TRIGGER_PATH;
import static com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController.FEIGN_EXCEPTION_TRIGGER_PATH;
import static com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController.INVALID_FORMAT_EXCEPTION_TRIGGER_PATH;
import static com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController.VALIDATION_EXCEPTION_TRIGGER_PATH;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.exceptionhandler.controller.ExceptionTriggerController;
import com.andersenlab.etalon.exceptionhandler.dto.common.enums.ExceptionType;
import com.andersenlab.etalon.exceptionhandler.dto.request.ExceptionTriggerRequest;
import com.andersenlab.etalon.exceptionhandler.exception.BusinessException;
import com.andersenlab.etalon.exceptionhandler.exception.EnumConversionException;
import com.andersenlab.etalon.exceptionhandler.exception.NotFoundException;
import com.andersenlab.etalon.exceptionhandler.exception.TechnicalException;
import com.andersenlab.etalon.exceptionhandler.exception.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import feign.FeignException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

@WebMvcTest(controllers = ExceptionTriggerController.class)
@Import(GlobalExceptionHandler.class)
class TestControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @ParameterizedTest
  @MethodSource("customExceptionsMethodSource")
  void whenTriggeringCustomException_thenReturnsDesiredCodeMessageAndException(
      ExceptionTriggerRequest trigger) throws Exception {
    mockMvc
        .perform(
            post(API_PREFIX + CUSTOM_EXCEPTION_TRIGGER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trigger)))
        .andExpect(status().is(trigger.exceptionType().getStatus().value()))
        .andExpect(
            result -> assertInstanceOf(trigger.exceptionClass(), result.getResolvedException()))
        .andExpect(jsonPath("$.message", is(trigger.message())));
  }

  static Stream<ExceptionTriggerRequest> customExceptionsMethodSource() {
    return Stream.of(
        ExceptionTriggerRequest.builder()
            .exceptionType(ExceptionType.BUSINESS)
            .message("I'm a message :)")
            .exceptionClass(BusinessException.class)
            .build(),
        ExceptionTriggerRequest.builder()
            .exceptionType(ExceptionType.ENUM_CONVERSION)
            .message("Enum conversion failed")
            .exceptionClass(EnumConversionException.class)
            .build(),
        ExceptionTriggerRequest.builder()
            .exceptionType(ExceptionType.TECHNICAL)
            .message("Failed to deserialize something")
            .exceptionClass(TechnicalException.class)
            .build(),
        ExceptionTriggerRequest.builder()
            .exceptionType(ExceptionType.NOT_FOUND)
            .message("Entity not found")
            .exceptionClass(NotFoundException.class)
            .build());
  }

  @Test
  void whenFeignExceptionThrown_thenReturnsBadGateway() throws Exception {
    mockMvc
        .perform(post(API_PREFIX + FEIGN_EXCEPTION_TRIGGER_PATH))
        .andExpect(status().isBadGateway())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(result -> assertInstanceOf(FeignException.class, result.getResolvedException()));
  }

  @Test
  void whenInvalidFormatExceptionThrown_thenReturnsBadRequest() throws Exception {
    mockMvc
        .perform(post(API_PREFIX + INVALID_FORMAT_EXCEPTION_TRIGGER_PATH))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertInstanceOf(InvalidFormatException.class, result.getResolvedException()));
  }

  @Test
  void whenMethodArgumentNotValidExceptionThrown_thenReturnsBadRequest() throws Exception {
    mockMvc
        .perform(
            post(API_PREFIX + VALIDATION_EXCEPTION_TRIGGER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertInstanceOf(
                    MethodArgumentNotValidException.class, result.getResolvedException()))
        .andExpect(jsonPath("$.violations").isArray())
        .andExpect(jsonPath("$.violations.length()", greaterThan(0)));
  }

  @Test
  void whenEnumConversionExceptionThrown_thenReturnsBadRequest() throws Exception {
    mockMvc
        .perform(post(API_PREFIX + ENUM_CONVERSION_EXCEPTION_TRIGGER_PATH))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertInstanceOf(EnumConversionException.class, result.getResolvedException()));
  }
}
