package com.andersenlab.etalon.infoservice.config.aop;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfiguration {
  @Bean
  @ConditionalOnProperty(prefix = "service-logging", name = "enabled", havingValue = "true")
  public ServiceAspect serviceAspect() {
    return new ServiceAspect();
  }

  @Bean
  @ConditionalOnProperty(prefix = "rest-controller-logging", name = "enabled", havingValue = "true")
  public RestControllerAspect restControllerAspect() {
    return new RestControllerAspect();
  }
}
