package com.andersenlab.etalon.infoservice.config;

import java.math.BigDecimal;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "exchange.coefficient")
public class ExchangeCoefficientsProperties {
  private BigDecimal buy;
  private BigDecimal sell;
}
