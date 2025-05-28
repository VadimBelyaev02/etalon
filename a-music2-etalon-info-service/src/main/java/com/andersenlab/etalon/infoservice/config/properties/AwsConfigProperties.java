package com.andersenlab.etalon.infoservice.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsConfigProperties {
  private String staticRegion;
  private String endpointUri;
  private String cognitoServerUrl;
  private String apiServerUrl;

  @Value("${cloud.aws.region.static}")
  private String awsRegion;

  @Bean
  public SesClient sesClient() {
    return SesClient.builder().region(Region.of(awsRegion)).build();
  }
}
