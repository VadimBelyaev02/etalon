package com.andersenlab.etalon.infoservice.component;

import com.andersenlab.etalon.infoservice.annotation.WithUserIdExtension;
import com.andersenlab.etalon.infoservice.component.config.SqsInitializer;
import com.andersenlab.etalon.infoservice.component.config.TestContainerManager;
import com.andersenlab.etalon.infoservice.component.config.WireMockInitializer;
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
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
    initializers = {WireMockInitializer.class, TestContainerManager.class, SqsInitializer.class})
@Sql(scripts = "file:src/test/resources/data/confirmations-initial-data.sql")
@ExtendWith(WithUserIdExtension.class)
public abstract class AbstractComponentTest {

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
