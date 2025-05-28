package com.andersenlab.etalon.infoservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "document.receipt")
public class DocumentReceiptProperties {
  @NestedConfigurationProperty private Transfer transfer = new Transfer();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Transfer {
    private String name;
  }
}
