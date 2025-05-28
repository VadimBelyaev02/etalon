package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.MockData;
import com.andersenlab.etalon.accountservice.controller.AccountController;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class UpdateAccountEndpointTest extends AbstractIntegrationTest {

  private static final Long ACCOUNT_ID = 2L;

  @Test
  void whenUpdateAccount_thenSuccess() throws Exception {
    // given
    final AccountRequestDto accountRequestDto = MockData.getValidAccountRequestDto();

    // when/then
    mockMvc
        .perform(
            patch(AccountController.ACCOUNT_V1_URL + "/" + ACCOUNT_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(accountRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Account updated successfully")));
  }
}
