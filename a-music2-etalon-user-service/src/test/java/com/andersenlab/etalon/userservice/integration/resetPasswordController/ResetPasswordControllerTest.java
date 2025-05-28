package com.andersenlab.etalon.userservice.integration.resetPasswordController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.controller.UserDataController;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.userservice.service.impl.ResetPasswordServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ResetPasswordControllerTest extends AbstractIntegrationTest {

  private static final String NON_EXISTING_EMAIL = "non-existing@example.com";

  @Test
  void whenRequestResetPasswordLinkWithExistingEmail_thenReturnSuccess() throws Exception {
    mockMvc
        .perform(
            post(UserDataController.USERS_URL + UserDataController.RESET_PASSWORD_LINK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidResetPasswordRequestDto())))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.message", is(ResetPasswordServiceImpl.PASSWORD_RESET_REQUEST_SUCCESS)));
  }

  @Test
  void whenRequestResetPasswordLinkWithNonExistingEmail_thenReturnNotFound() throws Exception {
    mockMvc
        .perform(
            post(UserDataController.USERS_URL + UserDataController.RESET_PASSWORD_LINK_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getNonExistingEmailResetPasswordRequestDto())))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.message",
                is(String.format(ResetPasswordServiceImpl.USER_NOT_FOUND, NON_EXISTING_EMAIL))));
  }

  @Test
  void whenConfirmResetPasswordWithValidToken_thenReturnSuccess() throws Exception {
    mockMvc
        .perform(
            post(UserDataController.USERS_URL + UserDataController.RESET_PASSWORD_CONFIRM_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidResetPasswordConfirmationRequestDto())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(ResetPasswordServiceImpl.PASSWORD_RESET_SUCCESS)));
  }

  @Test
  void whenConfirmResetPasswordWithInvalidToken_thenReturnBadRequest() throws Exception {
    mockMvc
        .perform(
            post(UserDataController.USERS_URL + UserDataController.RESET_PASSWORD_CONFIRM_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getInvalidTokenResetPasswordConfirmationRequestDto())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(ResetPasswordServiceImpl.RESET_TOKEN_INVALID)));
  }

  @Test
  void whenConfirmResetPasswordWithExpiredToken_thenReturnBadRequest() throws Exception {
    mockMvc
        .perform(
            post(UserDataController.USERS_URL + UserDataController.RESET_PASSWORD_CONFIRM_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getExpiredTokenResetPasswordConfirmationRequestDto())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(ResetPasswordServiceImpl.RESET_TOKEN_EXPIRED)));
  }
}
