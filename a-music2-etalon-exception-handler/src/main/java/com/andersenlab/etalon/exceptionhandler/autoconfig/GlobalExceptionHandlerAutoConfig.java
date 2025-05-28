package com.andersenlab.etalon.exceptionhandler.autoconfig;

import com.andersenlab.etalon.exceptionhandler.autoconfig.properties.ExceptionHandlerProperties;
import com.andersenlab.etalon.exceptionhandler.exception.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(GlobalExceptionHandler.class)
@ConditionalOnProperty(
    prefix = "exception.handler",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(ExceptionHandlerProperties.class)
public class GlobalExceptionHandlerAutoConfig {

  @Bean
  @ConditionalOnMissingBean
  public GlobalExceptionHandler globalExceptionHandler() {
    return new GlobalExceptionHandler();
  }
}
