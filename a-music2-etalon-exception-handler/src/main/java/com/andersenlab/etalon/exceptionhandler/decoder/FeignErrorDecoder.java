package com.andersenlab.etalon.exceptionhandler.decoder;

import static com.andersenlab.etalon.exceptionhandler.util.ExceptionConstants.JSON_DESERIALIZATION_ERROR;

import com.andersenlab.etalon.exceptionhandler.dto.common.response.ErrorResponseDto;
import com.andersenlab.etalon.exceptionhandler.exception.FeignClientException;
import com.andersenlab.etalon.exceptionhandler.exception.TechnicalException;
import com.andersenlab.etalon.exceptionhandler.util.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@AllArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

  private final ObjectMapper objectMapper;

  @Override
  public Exception decode(String methodKey, Response response) {
    log.error("Feign error occurred for method: {}", methodKey);
    System.out.println("test");
    try {
      if (Objects.nonNull(response.body())) {
        ErrorResponseDto errorResponse =
            objectMapper.readValue(response.body().asInputStream(), ErrorResponseDto.class);

        String errorMessage = errorResponse.message();
        String serviceIdentifier = errorResponse.serviceIdentifier();
        ErrorCode errorCode = ErrorCode.valueOf(errorResponse.errorCode());

        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        if (Objects.isNull(httpStatus)) {
          httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new FeignClientException(errorMessage, errorCode, serviceIdentifier, httpStatus);
      }
    } catch (IOException e) {
      log.error("Failed to deserialize Feign exception content: {}", e.getMessage());
      return new TechnicalException(JSON_DESERIALIZATION_ERROR.formatted(e.getMessage()));
    }

    return new TechnicalException(response.reason());
  }
}
