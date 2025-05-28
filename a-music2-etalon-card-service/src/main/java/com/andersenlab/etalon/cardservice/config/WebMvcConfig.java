package com.andersenlab.etalon.cardservice.config;

import com.andersenlab.etalon.cardservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.cardservice.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
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
}
