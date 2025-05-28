package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.annotation.WithUserId;
import com.andersenlab.etalon.accountservice.controller.AccountByNumberController;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class GetDetailedAccountInfoEndpointTest extends AbstractIntegrationTest {

  private static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";

  @Test
  @WithUserId
  void whenGetDetailedAccountInfo_thenSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(get(AccountByNumberController.ACCOUNT_BY_NUMBER_URL + "/" + ACCOUNT_NUMBER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.balance", is(0)))
        .andExpect(jsonPath("$.id", is(2)))
        .andExpect(jsonPath("$.iban", is("PL04234567840000000000000001")))
        .andExpect(jsonPath("$.userId", is("1")))
        .andExpect(jsonPath("$.isBlocked", is(false)));
  }
}
