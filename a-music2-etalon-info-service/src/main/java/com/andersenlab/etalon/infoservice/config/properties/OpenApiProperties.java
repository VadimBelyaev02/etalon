package com.andersenlab.etalon.infoservice.config.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "springdoc.service")
public class OpenApiProperties {

  private List<String> urls;
  private String apiServerUrl;
}
