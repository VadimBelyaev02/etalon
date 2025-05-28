package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.annotation.WithUserId;
import com.andersenlab.etalon.accountservice.controller.AccountByNumberController;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class GetAccountBalanceByAccountNumberEndpointTest extends AbstractIntegrationTest {

  public static final String ACCOUNT_NUMBER = "PL04234567840000000000000003";

  @Test
  @WithUserId
  void whenGetAccountBalanceByAccountNumber_thenSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            get(
                AccountByNumberController.ACCOUNT_BY_NUMBER_URL
                    + AccountByNumberController.ACCOUNT_BALANCE_BY_ACCOUNT_NUMBER_PATH_URL,
                ACCOUNT_NUMBER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountBalance", is(5000.0)));
  }
}
