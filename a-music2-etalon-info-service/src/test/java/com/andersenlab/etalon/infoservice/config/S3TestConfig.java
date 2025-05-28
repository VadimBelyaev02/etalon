package com.andersenlab.etalon.infoservice.config;

import static org.mockito.Mockito.mock;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class S3TestConfig {

  @Bean
  public AmazonS3 amazonS3() {
    return mock(AmazonS3.class);
  }
}
