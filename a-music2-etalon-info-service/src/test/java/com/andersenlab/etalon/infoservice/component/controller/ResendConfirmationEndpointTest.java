package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATIONS_URL;
import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATION_RESEND;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(scripts = "file:src/test/resources/data/confirmations-initial-data.sql")
public class ResendConfirmationEndpointTest extends AbstractComponentTest {
  private static final String TOO_MANY_REQUESTS_MESSAGE = "Too many requests of confirmation code.";
  private static final String CONFIRMATION_BLOCKED_MESSAGE = "Confirmation is blocked";
  private static final String VALID_USER_ID = "user";
  private static final Integer VALID_CONFIRMATION_ID = 1;
  private static final Long CONFIRMATION_ID_WITH_STATUS_CONFIRMED = 3L;
  private static final String VALID_CONFIRMATION_CODE = "123456";

  @Test
  @WithUserId(VALID_USER_ID)
  void shouldFailResending_whenNumberOfAttemptsExceeded() throws Exception {
    mockMvc
        .perform(
            post(CONFIRMATIONS_URL + "/12" + CONFIRMATION_RESEND)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isLocked())
        .andExpect(jsonPath("$.message", containsString(CONFIRMATION_BLOCKED_MESSAGE)))
        .andExpect(jsonPath("$.unblockDate").exists());
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void shouldFailResending_whenTooManyRequests() throws Exception {
    mockMvc
        .perform(
            post(CONFIRMATIONS_URL + "/7" + CONFIRMATION_RESEND)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message", containsString(TOO_MANY_REQUESTS_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenResendConfirmation_shouldReturnConfirmationResponse() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(CONFIRMATIONS_URL + "/2" + CONFIRMATION_RESEND)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.confirmationMethod")
                .value(ConfirmationMethod.EMAIL.toString()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.confirmationId", is(VALID_CONFIRMATION_ID)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void shouldFailResending_WhenStatusConfirmed() throws Exception {
    ConfirmationRequestDto verificationRequestWIthValidCode =
        new ConfirmationRequestDto(VALID_CONFIRMATION_CODE);
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        CONFIRMATIONS_URL
                            + "/"
                            + CONFIRMATION_ID_WITH_STATUS_CONFIRMED
                            + CONFIRMATION_RESEND)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(verificationRequestWIthValidCode)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is(BusinessException.CONFIRMATION_CONFLICT)));
  }
}
