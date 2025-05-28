package com.andersenlab.etalon.userservice.integration.validatorController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.controller.ValidatorController;
import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.PeselDto;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ValidatePeselBeforeRegistrationEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenPeselIsAvailableToRegister_shouldReturnTrue() throws Exception {
    // given
    final String response = "Pesel is available to register";
    final StatusMessageResponseDto dto = new StatusMessageResponseDto(true, response);
    final PeselDto request = new PeselDto("12345678918");

    // when/then
    mockMvc
        .perform(
            post(ValidatorController.VALIDATED_PESEL_BEFORE_REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.status", is(dto.status())))
        .andExpect(jsonPath("$.message", is(dto.message())));
  }

  @Test
  void whenNotAvailableToRegister_shouldReturnConflictAndMessage() throws Exception {
    // given
    final String response = "PESEL is already registered";
    final PeselDto request = new PeselDto("11111111111");

    // when/then
    mockMvc
        .perform(
            post(ValidatorController.VALIDATED_PESEL_BEFORE_REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.CONFLICT.value()))
        .andExpect(jsonPath("$.message", is(response)));
  }

  @ParameterizedTest
  @CsvSource({
    "123456789",
    "letterinput",
    "123456789 10",
    "' 12345678910'",
    " \"!@*&%\" ",
    "песель"
  })
  void whenNotValidInput_shouldReturnBadRequestAndMessage(final String providedInput)
      throws Exception {
    // given
    final String response = "Invalid format. PESEL number shall be 11 digits.";
    final PeselDto request = new PeselDto(providedInput);

    // when/then
    mockMvc
        .perform(
            post(ValidatorController.VALIDATED_PESEL_BEFORE_REGISTRATION_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(response)));
  }
}
