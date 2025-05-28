package com.andersenlab.etalon.exceptionhandler.config;

import com.andersenlab.etalon.exceptionhandler.decoder.FeignErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

  private final ObjectMapper objectMapper;

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder(objectMapper);
  }
}
