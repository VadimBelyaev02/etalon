package com.andersenlab.etalon.infoservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "document.statement")
public class DocumentStatementProperties {
  @NestedConfigurationProperty private Transaction transaction = new Transaction();
  @NestedConfigurationProperty private Transactions transactions = new Transactions();
  @NestedConfigurationProperty private Confirmation confirmation = new Confirmation();

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Transaction {
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Transactions {
    private String name;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Confirmation {
    private String name;
  }
}
