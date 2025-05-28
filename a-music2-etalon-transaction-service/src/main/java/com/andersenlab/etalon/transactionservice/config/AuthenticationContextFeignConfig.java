package com.andersenlab.etalon.transactionservice.config;

import com.andersenlab.etalon.transactionservice.interceptor.AuthenticationHolder;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
public class AuthenticationContextFeignConfig {

  private final AuthenticationHolder authenticationHolder;

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
    return new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3);
  }
}
