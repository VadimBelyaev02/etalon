package com.andersenlab.etalon.depositservice.config;

import com.andersenlab.etalon.depositservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.depositservice.interceptor.AuthenticationInterceptor;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
  private final TimeProvider timeProvider;

  @Bean
  public AuthenticationHolder authenticationHolder() {
    return new AuthenticationHolder();
  }

  @Bean
  public ThreadLocal<AuthenticationHolder> threadLocalAuthenticationHolder() {
    return ThreadLocal.withInitial(this::authenticationHolder);
  }

  @Bean
  public AuthenticationInterceptor authenticationInterceptor(
      ThreadLocal<AuthenticationHolder> threadLocalAuthenticationHolder) {
    return new AuthenticationInterceptor(threadLocalAuthenticationHolder);
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry
        .addInterceptor(authenticationInterceptor(threadLocalAuthenticationHolder()))
        .addPathPatterns("/**");
  }

  @PostConstruct
  public void setTimezone() {
    TimeZone.setDefault(TimeZone.getTimeZone(timeProvider.getZone()));
  }
}
