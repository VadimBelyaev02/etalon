package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TemplateController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class DeleteTemplateEndpointTest extends AbstractIntegrationTest {
  private static final Long EXISTING_TEMPLATE_ID = 1L;
  private static final Long ABSENT_TEMPLATE_ID = -1L;
  public static final String USER_ID = "1";

  @BeforeEach
  void setUp() {}

  @Test
  @WithUserId
  void whenDeleteTemplate_shouldSuccess() throws Exception {
    mockMvc
        .perform(delete(TemplateController.TEMPLATES_URL + "/" + EXISTING_TEMPLATE_ID))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                is(
                    MessageResponseDto.TEMPLATE_DELETE_IS_SUCCESSFUL.formatted(
                        EXISTING_TEMPLATE_ID))));
  }

  @Test
  @WithUserId
  void whenDeleteTemplate_shouldFail() throws Exception {
    mockMvc
        .perform(delete(TemplateController.TEMPLATES_URL + "/" + ABSENT_TEMPLATE_ID))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.message",
                is(BusinessException.TEMPLATE_NOT_FOUND_BY_ID.formatted(ABSENT_TEMPLATE_ID))));
  }
}
