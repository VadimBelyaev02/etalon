package com.andersenlab.etalon.userservice.integration.validatorController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.controller.ValidatorController;
import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.EmailDto;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ValidateEmailBeforeRegistrationEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenEmailIsAvailableToRegister_shouldReturnTrue() throws Exception {
    // given
    final String response = "Email is available to register";
    final StatusMessageResponseDto dto = new StatusMessageResponseDto(true, response);
    final EmailDto request = new EmailDto("valid@example.com");

    // when/then
    mockMvc
        .perform(
            post(ValidatorController.VALIDATED_EMAIL_BEFORE_REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.status", is(dto.status())))
        .andExpect(jsonPath("$.message", is(dto.message())));
  }

  @Test
  void whenEmailIsNotAvailableToRegister_shouldReturnBadRequestAndMessage() throws Exception {
    // given
    final String response = "Email is not available to register";
    final EmailDto request = new EmailDto("test01@gmail.com");

    // when/then
    mockMvc
        .perform(
            post(ValidatorController.VALIDATED_EMAIL_BEFORE_REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.CONFLICT.value()))
        .andExpect(jsonPath("$.message", is(response)));
  }
}
