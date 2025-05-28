package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.controller.AccountController;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class GetAllAccountNumbersEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenGetAllAccountsNumbers_thenSuccess() throws Exception {
    mockMvc
        .perform(get(AccountController.ACCOUNT_V1_URL).param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].accountNumber", is("PL04234567840000000000000001")))
        .andExpect(jsonPath("$[1].accountNumber", is("PL04234567840000000000000002")))
        .andExpect(jsonPath("$[2].accountNumber", is("PL04234567840000000000000003")));
  }
}
