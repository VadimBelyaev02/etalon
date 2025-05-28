package com.andersenlab.etalon.cardservice.integration;

import com.andersenlab.etalon.cardservice.annotation.WithUserIdExtension;
import com.andersenlab.etalon.cardservice.integration.config.TestContainerManager;
import com.andersenlab.etalon.cardservice.integration.config.WireMockInitializer;
import com.andersenlab.etalon.cardservice.repository.CardRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ContextConfiguration(initializers = {WireMockInitializer.class, TestContainerManager.class})
@ExtendWith(WithUserIdExtension.class)
@Sql(
    scripts = {
      "file:src/test/resources/data/products-initial-data.sql",
      "file:src/test/resources/data/cards-initial-data.sql",
      "file:src/test/resources/data/product-to-currency-initial-data.sql"
    })
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected CardRepository cardRepository;

  @Autowired protected ObjectMapper objectMapper;

  @Qualifier("info-service")
  @Autowired
  protected WireMockServer mockInfoService;

  @Qualifier("user-service")
  @Autowired
  protected WireMockServer mockUserService;

  @Qualifier("account-service")
  @Autowired
  protected WireMockServer mockAccountService;

  @Qualifier("deposit-service")
  @Autowired
  protected WireMockServer mockDepositService;

  protected <T> String toJson(final T object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
