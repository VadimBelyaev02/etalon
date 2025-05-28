package com.andersenlab.etalon.userservice.integration.emailModificationController;

import static com.andersenlab.etalon.userservice.controller.internal.EmailModificationInternalController.EMAIL_MODIFICATION_URI;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.annotation.WithUserId;
import com.andersenlab.etalon.userservice.controller.internal.EmailModificationInternalController;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class EmailModificationControllerTest extends AbstractIntegrationTest {

  public static final String VALID_USER_ID = "user";

  @Test
  void whenGetEmailModificationInfo_thenReturnEmailModificationInfo() throws Exception {
    mockMvc
        .perform(get(EmailModificationInternalController.URI + EMAIL_MODIFICATION_URI + "/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.newEmail", is("new_email@mail.com")));
  }

  @Test
  void whenGetEmailModificationInfo_thenReturnNotFound() throws Exception {
    mockMvc
        .perform(get(EmailModificationInternalController.URI + EMAIL_MODIFICATION_URI + "/0"))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenProcessEmailModification_shouldSuccess() throws Exception {
    mockMvc
        .perform(post(EmailModificationInternalController.URI + EMAIL_MODIFICATION_URI + "/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("An email has been updated for user with id user")));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenProcessEmailModification_shouldFail() throws Exception {
    mockMvc
        .perform(post(EmailModificationInternalController.URI + EMAIL_MODIFICATION_URI + "/0"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("User email modification id(0) does not exist")));
  }
}
