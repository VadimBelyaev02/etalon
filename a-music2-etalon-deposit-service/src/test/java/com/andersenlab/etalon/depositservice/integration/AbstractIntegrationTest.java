package com.andersenlab.etalon.depositservice.integration;

import com.andersenlab.etalon.depositservice.config.SqsInitializer;
import com.andersenlab.etalon.depositservice.config.TestContainerManager;
import com.andersenlab.etalon.depositservice.config.WireMockInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
    initializers = {WireMockInitializer.class, TestContainerManager.class, SqsInitializer.class})
@Transactional
public abstract class AbstractIntegrationTest {

  @Qualifier("account-service")
  @Autowired
  protected WireMockServer mockAccountService;

  @Qualifier("transaction-service")
  @Autowired
  protected WireMockServer mockTransactionService;

  @Rule public WireMockRule wireMockRule = new WireMockRule(0);

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
