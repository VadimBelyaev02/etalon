package com.andersenlab.etalon.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@Slf4j
public class CognitoConfig {

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean
  public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
    return CognitoIdentityProviderClient.builder()
        .region(Region.of(region))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }
}
