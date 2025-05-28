package com.andersenlab.etalon.userservice.integration.userDataController;

import static com.andersenlab.etalon.userservice.controller.UserDataController.USER_EMAIL_MODIFICATION;
import static com.andersenlab.etalon.userservice.exception.BusinessException.EMAIL_CAN_NOT_MATCH_WITH_CURRENT_ONE;
import static com.andersenlab.etalon.userservice.exception.BusinessException.EMAIL_IS_ALREADY_REGISTERED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.annotation.WithUserId;
import com.andersenlab.etalon.userservice.controller.UserDataController;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.userservice.repository.EmailModificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class EmailModificationServiceIntegrationTest extends AbstractIntegrationTest {

  private static final String USER_ID = "user";
  private static final String EMAIL = "test@mail.com";
  private static final String MATCHED_EMAIL = "test01@gmail.com";
  private static final String ALREADY_REGISTERED_EMAIL = "test02@gmail.com";

  @Autowired private EmailModificationRepository emailModificationRepository;

  @Test
  @WithUserId(USER_ID)
  void shouldSaveEmailModification_thenReturnModificationId() throws Exception {
    UserEmailModificationRequestDto requestDto = new UserEmailModificationRequestDto(EMAIL);

    mockMvc
        .perform(
            post(UserDataController.USERS_URL + UserDataController.USER_EMAIL_MODIFICATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.confirmationId").exists());
    assertThat(
            emailModificationRepository.findAll().stream()
                .filter(em -> em.getNewEmail().equalsIgnoreCase(EMAIL))
                .toList())
        .isNotEmpty();
  }

  @Test
  @WithUserId(USER_ID)
  void whenRequestEmailModificationWithMatchedEmail_shouldReturnBadRequest() throws Exception {
    UserEmailModificationRequestDto requestDto = new UserEmailModificationRequestDto(MATCHED_EMAIL);

    mockMvc
        .perform(
            post(UserDataController.USERS_URL + USER_EMAIL_MODIFICATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is(EMAIL_CAN_NOT_MATCH_WITH_CURRENT_ONE)));
  }

  @Test
  @WithUserId(USER_ID)
  void whenRequestEmailModificationWithAlreadyRegisteredEmail_shouldReturnBadRequest()
      throws Exception {
    UserEmailModificationRequestDto requestDto =
        new UserEmailModificationRequestDto(ALREADY_REGISTERED_EMAIL);

    mockMvc
        .perform(
            post(UserDataController.USERS_URL + USER_EMAIL_MODIFICATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is(EMAIL_IS_ALREADY_REGISTERED)));
  }
}
