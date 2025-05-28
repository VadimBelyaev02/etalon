package com.andersenlab.etalon.userservice.integration.userDataController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.annotation.WithUserId;
import com.andersenlab.etalon.userservice.controller.UserDataController;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class UserDataEndpointTest extends AbstractIntegrationTest {
  private static final String USER_ID = "user";
  private UserDataResponseDto userData;

  @BeforeEach
  void setUp() {
    userData = MockData.getValidUserData();
  }

  @Test
  @WithUserId(USER_ID)
  void whenValidRequest_thenReturnUserDataResponseDto() throws Exception {
    // when/then
    mockMvc
        .perform(
            get(UserDataController.USERS_URL + "/" + USER_ID + UserDataController.DETAILS_URI)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(userData.id())))
        .andExpect(jsonPath("$.firstName", is(userData.firstName())))
        .andExpect(jsonPath("$.lastName", is(userData.lastName())))
        .andExpect(jsonPath("$.pesel", is(userData.pesel())))
        .andExpect(jsonPath("$.email", is(userData.email())))
        .andExpect(
            jsonPath(
                "$.createAt",
                is(userData.createAt().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
  }

  @Test
  @WithUserId("sth")
  void whenUserNotExist_thenReturnBadRequestAndMessage() throws Exception {
    // given
    final String message = "User with such credentials (sth) does not exist";

    // when/then
    mockMvc
        .perform(
            get(UserDataController.USERS_URL + "/sth" + UserDataController.DETAILS_URI)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(message)));
  }
}
