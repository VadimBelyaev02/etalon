package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.MockData;
import com.andersenlab.etalon.accountservice.controller.AccountController;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class AddNewAccountEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenAddNewIban_thenSuccess() throws Exception {
    // given
    final AccountResponseDto response = MockData.getValidAccountResponseDto();
    final AccountCreationRequestDto request = MockData.getValidAccountCreationRequestDto();

    // when/then
    mockMvc
        .perform(
            post(AccountController.ACCOUNT_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.CREATED.value()))
        .andExpect(jsonPath("$.id", is(Math.toIntExact(response.id()))))
        .andExpect(jsonPath("$.currency", is(response.currency().toString())))
        .andExpect(jsonPath("$.isBlocked", is(response.isBlocked())))
        .andExpect(jsonPath("$.status", is(response.status().toString())))
        .andExpect(jsonPath("$.accountType", is(response.accountType().toString())));
  }
}
