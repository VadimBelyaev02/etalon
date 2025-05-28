package com.andersenlab.etalon.infoservice.config;

import com.andersenlab.etalon.infoservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.infoservice.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
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
    return new AuthenticationInterceptor(threadLocalAuthenticationHolder.get());
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry
        .addInterceptor(authenticationInterceptor(threadLocalAuthenticationHolder()))
        .addPathPatterns("/**");
  }
}
