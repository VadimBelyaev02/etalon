package com.andersenlab.etalon.infoservice.config;

import com.andersenlab.etalon.infoservice.config.properties.OpenApiProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
  private final OpenApiProperties openApiProperties;

  @Value("${cloud.aws.cognito-server-url}")
  private String cognitoServerUrl;

  @Value("${springdoc.packagesToScan}")
  private String packagesToScan;

  private static final String OAUTH2_SCHEME_NAME = "OAuth2";
  private static final String OAUTH2_AUTHORIZE_ENDPOINT = "/oauth2/authorize";
  private static final String OAUTH2_TOKEN_ENDPOINT = "/oauth2/token";
  private static final String OAUTH2_REFRESH_ENDPOINT = "/oauth2/refresh";
  private static final String OAUTH2_SCOPE_NAME = "aws.cognito.signin.user.admin";
  private static final String OAUTH2_SCOPE_DESCRIPTION =
      "Grants administrative access to manage user accounts";

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .servers(List.of(new Server().url(openApiProperties.getApiServerUrl())))
        .components(
            new Components()
                .addSecuritySchemes(
                    OAUTH2_SCHEME_NAME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .flows(
                            new OAuthFlows()
                                .authorizationCode(
                                    new OAuthFlow()
                                        .authorizationUrl(
                                            cognitoServerUrl + OAUTH2_AUTHORIZE_ENDPOINT)
                                        .tokenUrl(cognitoServerUrl + OAUTH2_TOKEN_ENDPOINT)
                                        .refreshUrl(cognitoServerUrl + OAUTH2_REFRESH_ENDPOINT)
                                        .scopes(
                                            new Scopes()
                                                .addString(
                                                    OAUTH2_SCOPE_NAME,
                                                    OAUTH2_SCOPE_DESCRIPTION))))))
        .addSecurityItem(
            new SecurityRequirement().addList(OAUTH2_SCHEME_NAME, List.of(OAUTH2_SCOPE_NAME)));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("info-service")
        .pathsToMatch("/api/v1/**")
        .packagesToScan(packagesToScan)
        .build();
  }
}
