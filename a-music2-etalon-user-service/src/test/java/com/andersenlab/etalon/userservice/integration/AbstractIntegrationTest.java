package com.andersenlab.etalon.userservice.integration;

import com.andersenlab.etalon.userservice.annotation.WithUserIdExtension;
import com.andersenlab.etalon.userservice.config.CognitoTestConfig;
import com.andersenlab.etalon.userservice.config.TestContainerManager;
import com.andersenlab.etalon.userservice.config.WireMockInitializer;
import com.andersenlab.etalon.userservice.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Import(CognitoTestConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ExtendWith(WithUserIdExtension.class)
@ContextConfiguration(initializers = {WireMockInitializer.class, TestContainerManager.class})
@Sql(
    scripts = {
      "classpath:data/users-initial-data.sql",
      "classpath:data/registration-order-initial-data.sql",
      "classpath:data/email-modification-initial-data.sql",
      "classpath:data/reset-password-initial-data.sql"
    })
public abstract class AbstractIntegrationTest {

  @Autowired protected UserRepository userRepository;
  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;

  protected <T> String toJson(final T object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }
}
