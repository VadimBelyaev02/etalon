package com.andersenlab.etalon.transactionservice.integration;

import com.andersenlab.etalon.transactionservice.annotation.WithUserIdExtension;
import com.andersenlab.etalon.transactionservice.config.SqsInitializer;
import com.andersenlab.etalon.transactionservice.config.TestContainerManager;
import com.andersenlab.etalon.transactionservice.config.WireMockInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(
    initializers = {WireMockInitializer.class, TestContainerManager.class, SqsInitializer.class})
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(WithUserIdExtension.class)
@Sql(
    scripts = {
      "file:src/test/resources/data/transaction-initial-data.sql",
      "file:src/test/resources/data/event-initial-data.sql",
      "file:src/test/resources/data/transfers-initial-data.sql",
      "file:src/test/resources/data/payments-initial-data.sql",
      "file:src/test/resources/data/templates-initial-data.sql",
      "file:src/test/resources/data/scheduled-transfer-initial-data.sql"
    })
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;

  protected <T> String toJson(final T object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("cloud.aws.endpointUri", () -> SqsInitializer.address);
  }
}
