package com.andersenlab.etalon.depositservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cloud.aws")
public class AwsSqsConfigProperties {
  private String staticRegion;
  private String endpointUri;
}
