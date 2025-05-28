package com.andersenlab.etalon.userservice.config;

import com.andersenlab.etalon.userservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.userservice.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Bean
  public AuthenticationHolder authenticationHolder() {
    return new AuthenticationHolder();
  }

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry
        .addInterceptor(authenticationInterceptor(new AuthenticationHolder()))
        .addPathPatterns("/**");
  }

  @Bean
  public AuthenticationInterceptor authenticationInterceptor(
      AuthenticationHolder authenticationHolder) {
    return new AuthenticationInterceptor(authenticationHolder);
  }

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public AuthenticationHolder userIdHolder() {
    return new AuthenticationHolder();
  }

  @Bean
  public ThreadLocal<AuthenticationHolder> threadLocalAuthenticationHolder() {
    return ThreadLocal.withInitial(this::authenticationHolder);
  }
}
