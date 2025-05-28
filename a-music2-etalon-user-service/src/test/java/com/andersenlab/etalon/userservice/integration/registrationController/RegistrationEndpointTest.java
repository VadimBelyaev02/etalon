package com.andersenlab.etalon.userservice.integration.registrationController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.annotation.WithUserId;
import com.andersenlab.etalon.userservice.controller.RegistrationController;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class RegistrationEndpointTest extends AbstractIntegrationTest {

  public static final String VALID_USER_ID = "user";

  @Test
  void whenCompleteRegistration_shouldSuccess() throws Exception {
    mockMvc
        .perform(
            post(
                    RegistrationController.REGISTRATIONS_URL
                        + RegistrationController.COMPLETE_REGISTRATION_ID,
                    "dedcb23a-85cf-4746-8c31-6ccaf59fd969")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidCompleteRegistrationRequestDto())))
        .andExpect(status().isOk());
  }

  @Test
  void whenInitiateRegistration_shouldSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            post(RegistrationController.REGISTRATIONS_URL + RegistrationController.COGNITO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidInitiateRegistrationRequestIntegration())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.confirmationId", is(1)))
        .andExpect(jsonPath("$.maskedEmail", is("r****@proton.me")))
        .andExpect(jsonPath("$.orderStatus", is("PENDING_CONFIRMATION")));
  }

  @Test
  void whenInitiateRegistrationWhenCodeConfirmed_shouldSuccess() throws Exception {
    mockMvc
        .perform(
            post(RegistrationController.REGISTRATIONS_URL + RegistrationController.COGNITO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getInitiateRegistrationRequestWithCodeConfirmed())))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.confirmationId", is(1)))
        .andExpect(jsonPath("$.maskedEmail", is("o****@proton.me")))
        .andExpect(jsonPath("$.orderStatus", is("CONFIRMED")));
  }

  @Test
  void whenInitiateRegistrationWhenUserExists_shouldFail() throws Exception {
    mockMvc
        .perform(
            post(RegistrationController.REGISTRATIONS_URL + RegistrationController.COGNITO)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getAlreadyRegisteredInitiateRegistrationRequestDto())))
        .andExpect(status().isConflict());
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenInitiateRegistrationWithInvalidPesel_shouldBadRequest() throws Exception {
    // given
    final String response = "Pesel must contain only digits";

    // when/then
    mockMvc
        .perform(
            post(RegistrationController.REGISTRATIONS_URL_INITIATE)
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getExtraDigitInitiateRegistrationRequestDto())))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(response)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenInitiateRegistrationWithInvalidPesel_shouldNotFound() throws Exception {
    // given
    final String response = "Client with provided pesel not found";

    // when/then
    mockMvc
        .perform(
            post(RegistrationController.REGISTRATIONS_URL_INITIATE)
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getInvalidInitiateRegistrationRequestDto())))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.message", is(response)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenInitiateRegistrationWithInvalidDataType_shouldBadRequest() throws Exception {
    // given
    final String response =
        "Passed value [accepted] is invalid for fields [isPrivacyPolicyAccepted]. Possible values are: [true, false]";

    // when/then
    mockMvc
        .perform(
            post(RegistrationController.REGISTRATIONS_URL_INITIATE)
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"isPrivacyPolicyAccepted\": \"accepted\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(response)));
  }
}
