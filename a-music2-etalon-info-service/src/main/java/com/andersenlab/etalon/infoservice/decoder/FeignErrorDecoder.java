package com.andersenlab.etalon.infoservice.decoder;

import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.exception.SystemException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {
  private final ObjectMapper objectMapper;
  private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

  @Override
  public Exception decode(String methodKey, Response response) {
    HttpStatus responseStatus = HttpStatus.valueOf(response.status());
    log.error("{handleFeignException} -> Got {} response from {}", response.status(), methodKey);
    if (responseStatus.is4xxClientError()) {
      MessageResponseDto responseDto;
      try (InputStream bodyIs = response.body().asInputStream()) {
        responseDto = objectMapper.readValue(bodyIs, MessageResponseDto.class);
      } catch (IOException e) {
        Exception exception = defaultDecoder.decode(methodKey, response);
        log.error("{handleFeignException:IOException} -> {}", exception.getMessage());
        return new SystemException(responseStatus, SystemException.INTERNAL_SERVER_ERROR);
      }
      log.error("{handleFeignException:4xxClientError} -> {}", responseDto.message());
      return new BusinessException(responseStatus, responseDto.message());
    }
    Exception exception = defaultDecoder.decode(methodKey, response);
    log.error("{handleFeignException} -> {}", exception.getMessage());
    return new SystemException(responseStatus, SystemException.INTERNAL_SERVER_ERROR);
  }
}
