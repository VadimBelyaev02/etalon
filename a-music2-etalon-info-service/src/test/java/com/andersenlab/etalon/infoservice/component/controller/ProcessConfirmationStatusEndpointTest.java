package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATIONS_URL;
import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATION_EMAIL;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ProcessConfirmationStatusEndpointTest extends AbstractComponentTest {

  private static final String CONFIRMATION_BLOCKED_MESSAGE = "Confirmation is blocked";
  private static final String CONFIRMATION_NOT_FOUND_MESSAGE =
      "Confirmation request with id 20 is not found";
  private static final String CONFIRMATION_SUCCESS_MESSAGE =
      "An email has been updated for user with id user";
  private static final String VALID_USER_ID = "user";

  @Test
  @WithUserId(VALID_USER_ID)
  void whenProcessConfirmationStatus_shouldSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            post(CONFIRMATIONS_URL + "/14" + CONFIRMATION_EMAIL)
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidConfirmationRequestDto())))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is(CONFIRMATION_SUCCESS_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenConfirmationConfirmed_shouldReturnDeletedResponseStatus() throws Exception {
    // when/then
    mockMvc
        .perform(
            post(CONFIRMATIONS_URL + "/20" + CONFIRMATION_EMAIL)
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidConfirmationRequestDto())))
        .andDo(print())
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.message", containsString(CONFIRMATION_NOT_FOUND_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenConfirmationBlocked_userShouldWait10MinBeforeRetrying() throws Exception {
    // when/then
    mockMvc
        .perform(
            post(CONFIRMATIONS_URL + "/12" + CONFIRMATION_EMAIL)
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(MockData.getValidConfirmationRequestDto())))
        .andExpect(status().isLocked())
        .andExpect(jsonPath("$.message", containsString(CONFIRMATION_BLOCKED_MESSAGE)))
        .andExpect(jsonPath("$.unblockDate").exists());
  }
}
