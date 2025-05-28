package com.andersenlab.etalon.accountservice.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.annotation.WithUserId;
import com.andersenlab.etalon.accountservice.controller.AccountByNumberController;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

class ChangeIsBlockedEndpointTest extends AbstractIntegrationTest {

  @Test
  @WithUserId
  void whenChangeIsBlockedOfAccountWithStatusBlocked_thenSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            put(AccountByNumberController.ACCOUNT_BY_NUMBER_URL
                    + AccountByNumberController.BLOCKING_URI)
                .param("accountNumber", "PL04234567840000000000000004"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }

  @Test
  @WithUserId
  void whenChangeIsBlockedOfAccountWithStatusUnblocked_thenSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            put(AccountByNumberController.ACCOUNT_BY_NUMBER_URL
                    + AccountByNumberController.BLOCKING_URI)
                .param("accountNumber", "PL04234567840000000000000003"))
        .andExpect(status().isOk())
        .andExpect(content().string("true"));
  }
}
