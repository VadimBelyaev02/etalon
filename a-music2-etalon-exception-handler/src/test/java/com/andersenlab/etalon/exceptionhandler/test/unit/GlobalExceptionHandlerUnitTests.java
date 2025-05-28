package com.andersenlab.etalon.exceptionhandler.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.andersenlab.etalon.exceptionhandler.dto.common.response.ErrorResponseDto;
import com.andersenlab.etalon.exceptionhandler.exception.BusinessException;
import com.andersenlab.etalon.exceptionhandler.exception.EnumConversionException;
import com.andersenlab.etalon.exceptionhandler.exception.handler.GlobalExceptionHandler;
import feign.FeignException;
import feign.Request;
import feign.Response;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerUnitTests {

  @InjectMocks private GlobalExceptionHandler handler;

  @Test
  void whenHandleBusinessException_thenReturnsStatusFromExceptionAndMessage() {
    String message = "Some business rules are violated";
    BusinessException ex = new BusinessException(message);

    ResponseEntity<ErrorResponseDto> response = handler.handleBusinessException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo(message);
  }

  @Test
  void whenHandleFeignException_thenReturnFeignStatusAndJsonContentType() {
    Request request = Request.create(Request.HttpMethod.GET, "/test", Map.of(), null, null, null);
    Response feignResponse =
        Response.builder()
            .status(500)
            .reason("Internal server error")
            .request(request)
            .headers(Map.of())
            .build();
    FeignException feignEx = FeignException.errorStatus("testKey", feignResponse);

    ResponseEntity<ErrorResponseDto> response = handler.handleFeignException(feignEx);
    HttpHeaders headers = response.getHeaders();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo(feignEx.getMessage());
  }

  @Test
  @SneakyThrows
  void whenHandleMethodArgumentNotValidException_thenReturnFirstFieldErrorMessage() {
    String objectName = "testObject";
    String validationMessage = "must not be blank";
    BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), objectName);
    bindingResult.addError(new FieldError(objectName, "information", validationMessage));

    Method method = this.getClass().getDeclaredMethod("dummyMethod", String.class);
    MethodParameter param = new MethodParameter(method, 0);
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

    ResponseEntity<ErrorResponseDto> response = handler.handleMethodArgumentNotValidException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo(validationMessage);
  }

  @Test
  void whenHandleEnumConversionException_thenReturnBadRequestAndMessage() {
    String msg = "Enum conversion failed for ABC";
    EnumConversionException ex = new EnumConversionException(msg);

    ResponseEntity<ErrorResponseDto> response = handler.handleEnumConversionException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo(msg);
  }

  @SuppressWarnings("unused")
  private void dummyMethod(String param) {}
}
