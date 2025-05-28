package com.andersenlab.etalon.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${api-docs.server.name}")
  private String serverUrl;

  @Value("${spring.application.name}")
  private String tag;

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .tags(List.of(new Tag().name(tag)))
        .servers(List.of(new Server().url(serverUrl)));
  }
}
