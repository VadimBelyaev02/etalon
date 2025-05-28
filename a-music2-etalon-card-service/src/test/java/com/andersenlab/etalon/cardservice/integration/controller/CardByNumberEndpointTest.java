package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardByNumberController;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class CardByNumberEndpointTest extends AbstractIntegrationTest {
  private static final String USER_ID = "user";

  @Test
  @WithUserId(USER_ID)
  void whenGetAccountBalanceByAccountNumber_shouldSuccess() throws Exception {
    mockMvc
        .perform(
            get(CardByNumberController.USER_CARD_BY_NUMBER_URL, "4246700000000019")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(2.14)));
  }
}
