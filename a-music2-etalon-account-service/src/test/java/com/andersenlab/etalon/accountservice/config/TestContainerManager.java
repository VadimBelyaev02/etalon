package com.andersenlab.etalon.accountservice.config;

import jakarta.annotation.PreDestroy;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

@Component
public class TestContainerManager
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  static PostgreSQLContainer<?> postgresqlContainer =
      new PostgreSQLContainer<>("postgres:14-alpine")
          .withDatabaseName("etalon")
          .withUsername("postgres")
          .withPassword("postgres")
          .withInitScript("data/test-postgres-init.sql");

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {

    postgresqlContainer.start();
    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "spring.datasource.url=" + postgresqlContainer.getJdbcUrl(),
        "spring.datasource.username=" + postgresqlContainer.getUsername(),
        "spring.datasource.password=" + postgresqlContainer.getPassword());
  }

  @PreDestroy
  public void stop() {
    postgresqlContainer.stop();
  }
}
