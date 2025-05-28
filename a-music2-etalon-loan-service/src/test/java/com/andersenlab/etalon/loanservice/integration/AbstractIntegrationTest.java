package com.andersenlab.etalon.loanservice.integration;

import com.andersenlab.etalon.loanservice.annotation.WithUserIdExtension;
import com.andersenlab.etalon.loanservice.config.TestContainerManager;
import com.andersenlab.etalon.loanservice.config.WireMockInitializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@Sql(
    scripts = {
      "file:src/test/resources/data/orders-initial-data.sql",
      "file:src/test/resources/data/loans-initial-data.sql"
    })
@ExtendWith(WithUserIdExtension.class)
public abstract class AbstractIntegrationTest {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;

  protected <T> String toJson(final T object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  @PostConstruct
  public void init() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
