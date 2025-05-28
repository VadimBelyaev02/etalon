package com.andersenlab.etalon.cardservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OpenApiConfig implements WebMvcConfigurer {

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
