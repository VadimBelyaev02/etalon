package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.ConfirmationController.CONFIRMATIONS_URI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.controller.internal.ConfirmationInternalController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class ConfirmationInternalControllerTest extends AbstractComponentTest {

  public static final String VALID_USER_ID = "user";

  @Test
  @WithUserId(VALID_USER_ID)
  void whenGetConfirmationsByOperationAndTargetId_shouldSucceed() throws Exception {
    mockMvc
        .perform(
            get(ConfirmationInternalController.URI + CONFIRMATIONS_URI + "/EMAIL_MODIFICATION/5")
                .header("authenticated-user-id", VALID_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.length()").value(3))
        .andExpect(jsonPath("$[0].confirmationId").value(12));
  }
}
