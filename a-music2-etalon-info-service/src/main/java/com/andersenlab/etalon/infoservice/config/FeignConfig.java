package com.andersenlab.etalon.infoservice.config;

import com.andersenlab.etalon.infoservice.decoder.FeignErrorDecoder;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

  private final AuthenticationHolder authenticationHolder;
  private final ObjectMapper objectMapper;

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder(objectMapper);
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate ->
        Optional.ofNullable(authenticationHolder.getUserId())
            .ifPresent(
                userId ->
                    requestTemplate.header(
                        "authenticated-user-id", authenticationHolder.getUserId()));
  }

  @Bean
  public Request.Options feignOptions() {
    return new Request.Options(10000, TimeUnit.MILLISECONDS, 10000, TimeUnit.MILLISECONDS, true);
  }

  @Bean
  public Retryer feignRetryer() {
    return Retryer.NEVER_RETRY;
  }
}
