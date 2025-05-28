package com.andersenlab.etalon.infoservice.config;

import static org.mockito.Mockito.mock;

import com.andersenlab.etalon.infoservice.sqs.SqsMessageProducer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class SqsTestConfig {

  @Bean
  @Primary
  public SqsMessageProducer sqsMessageProducer() {
    return mock(SqsMessageProducer.class);
  }
}
