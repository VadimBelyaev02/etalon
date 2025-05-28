package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TemplateController;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class GetTemplatesEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "1";
  public static final String TEMPLATE_ID = "/1";

  @Test
  @WithUserId
  void whenGetTemplates_thenSuccess() throws Exception {
    mockMvc
        .perform(get(TemplateController.TEMPLATES_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].templateId", is(2)))
        .andExpect(jsonPath("$[0].productId", is(3)))
        .andExpect(jsonPath("$[0].templateName", is("name2")))
        .andExpect(jsonPath("$[0].description", is("desc")))
        .andExpect(jsonPath("$[0].amount", is(10)))
        .andExpect(jsonPath("$[0].templateType", is("TRANSFER")))
        .andExpect(jsonPath("$[0].source", is("PL04234567840000000000000001")))
        .andExpect(jsonPath("$[0].destination", is("PL04234567840000000000000002")))
        .andExpect(jsonPath("$[0].createAt", is("2023-11-06T20:06:47.262178Z")))
        .andExpect(jsonPath("$[1].templateId", is(1)))
        .andExpect(jsonPath("$[1].productId", is(1)))
        .andExpect(jsonPath("$[1].templateName", is("name1")))
        .andExpect(jsonPath("$[1].description", is("desc")))
        .andExpect(jsonPath("$[1].amount", is(10)))
        .andExpect(jsonPath("$[1].templateType", is("PAYMENT")))
        .andExpect(jsonPath("$[1].destination", is("+48121234567")))
        .andExpect(jsonPath("$[1].createAt", is("2023-11-05T20:06:47.262178Z")));
  }

  @Test
  @WithUserId
  void whenGetTemplate_thenSuccess() throws Exception {
    mockMvc
        .perform(
            get(TemplateController.TEMPLATES_URL + TEMPLATE_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.templateId", is(1)))
        .andExpect(jsonPath("$.productId", is(1)))
        .andExpect(jsonPath("$.templateName", is("name1")))
        .andExpect(jsonPath("$.description", is("desc")))
        .andExpect(jsonPath("$.amount", is(10)))
        .andExpect(jsonPath("$.templateType", is("PAYMENT")))
        .andExpect(jsonPath("$.createAt", is("2023-11-05T20:06:47.262178Z")))
        .andExpect(jsonPath("$.destination", is("+48121234567")));
  }
}
