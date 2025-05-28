package com.andersenlab.etalon.userservice.integration.userDataController;

import static com.andersenlab.etalon.userservice.controller.UserDataController.USERS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.annotation.WithUserId;
import com.andersenlab.etalon.userservice.controller.UserDataController;
import com.andersenlab.etalon.userservice.dto.user.request.EmploymentDataDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserPatchRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.integration.AbstractIntegrationTest;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

class PatchUserDataEndPointTest extends AbstractIntegrationTest {
  private static final String USER_ID = "full-user";
  private static final String NULL_USER = "null-user";
  private static final String EXPECTED_MESSAGE = "User with id: %s updated";
  private UserPatchRequestDto request;
  private UserDataResponseDto userData;

  @BeforeEach
  void setUp() {
    userData = MockData.getFulfilledUserData();
    request = MockData.getUserPatchRequestDto();
  }

  @Test
  @WithUserId(USER_ID)
  void whenPatchRequestWithAllNonNullFields_thenEmploymentDataChanged() throws Exception {
    // when
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(EXPECTED_MESSAGE.formatted(USER_ID))))
        .andReturn();

    // then
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
        .andExpect(jsonPath("$.employmentData.position", is(request.employmentData().position())))
        .andExpect(
            jsonPath("$.employmentData.placeOfWork", is(request.employmentData().placeOfWork())));
  }

  @Test
  @WithUserId(NULL_USER)
  void whenPatchRequestWithAllNonNullFieldsToUserWithNullFields_thenEmploymentDataChanged()
      throws Exception {
    // when
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(EXPECTED_MESSAGE.formatted(NULL_USER))))
        .andReturn();

    // then
    mockMvc
        .perform(
            get(UserDataController.USERS_URL + "/" + NULL_USER + UserDataController.DETAILS_URI)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(NULL_USER)))
        .andExpect(jsonPath("$.employmentData.position", is(request.employmentData().position())))
        .andExpect(
            jsonPath("$.employmentData.placeOfWork", is(request.employmentData().placeOfWork())));
  }

  @Test
  @WithUserId(USER_ID)
  void whenPatchRequestWithPosition_thenPositionChanged() throws Exception {
    // when
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    toJson(
                        UserPatchRequestDto.builder()
                            .employmentData(
                                EmploymentDataDto.builder()
                                    .position(request.employmentData().position())
                                    .build())
                            .build())))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(EXPECTED_MESSAGE.formatted(USER_ID))))
        .andReturn();

    // then
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
        .andExpect(jsonPath("$.phoneNumber", is(userData.phoneNumber())))
        .andExpect(jsonPath("$.address.city", is(userData.address().city())))
        .andExpect(jsonPath("$.address.voivodeship", is(userData.address().voivodeship())))
        .andExpect(jsonPath("$.address.building", is(userData.address().building())))
        .andExpect(jsonPath("$.address.apartment", is(userData.address().apartment())))
        .andExpect(jsonPath("$.address.street", is(userData.address().street())))
        .andExpect(jsonPath("$.address.postCode", is(userData.address().postCode())))
        .andExpect(jsonPath("$.employmentData.position", is(request.employmentData().position())))
        .andExpect(
            jsonPath("$.employmentData.placeOfWork", is(userData.employmentData().placeOfWork())));
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestDto_thenSuccess() throws Exception {
    // given
    UserPatchRequestDto request = MockData.getUserPatchRequestDto();

    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(EXPECTED_MESSAGE.formatted(USER_ID))));
  }

  @ParameterizedTest
  @MethodSource("invalidUserPatchRequest")
  @WithUserId("full-user")
  void whenUserPatchRequestDto_thenFail(UserPatchRequestDto request) throws Exception {
    // when
    // then
    mockMvc
        .perform(patch(USERS_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestWithIntegerDataDto_thenBadRequest() throws Exception {
    // given
    UserPatchRequestDto request = MockData.getUserPatchRequestWithWrongDataDto();

    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestWithUnknownField_thenBadRequest() throws Exception {
    // given
    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "someUnexpectedField": "Invalid Field"
                }
                """))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestWithNullFields_thenSuccess() throws Exception {
    // given
    UserPatchRequestDto request = MockData.getUserPatchRequestDtoWithNullFields();

    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestWithWrongPosition_thenBadRequest() throws Exception {
    // given
    UserPatchRequestDto request = MockData.getUserPatchRequestWithWrongPositionDto();

    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Size must be between 2 and 50")));
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestWithInvalidCharacter_thenBadRequest() throws Exception {
    // given
    UserPatchRequestDto userPatchRequest = MockData.getUserPatchRequestWithInvalidCharacter();

    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userPatchRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.message",
                is(
                    "Position may only contain Latin letters, digits, spaces, special characters (only - \" , ' ( ) . « » ).")));
  }

  @Test
  @WithUserId("full-user")
  void whenUserPatchRequestStartingWithHyphen_thenBadRequest() throws Exception {
    // given
    UserPatchRequestDto userPatchRequest = MockData.getUserPatchRequestStartingWithHyphen();

    // when
    // then
    mockMvc
        .perform(
            patch(UserDataController.USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(userPatchRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message", is("Position cannot start or end with a space or hyphen.")));
  }

  @Test
  @WithUserId("user")
  void whenRequestUserData_thenSuccess() throws Exception {
    // given
    final UserDataResponseDto expected = MockData.getValidUserData();

    // when
    // then
    mockMvc
        .perform(get(UserDataController.USERS_URL + "/user" + UserDataController.DETAILS_URI))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(expected.id())))
        .andExpect(jsonPath("$.firstName", is(expected.firstName())))
        .andExpect(jsonPath("$.lastName", is(expected.lastName())))
        .andExpect(jsonPath("$.pesel", is(expected.pesel())))
        .andExpect(jsonPath("$.email", is(expected.email())))
        .andExpect(
            jsonPath(
                "$.createAt", is(expected.createAt().format(DateTimeFormatter.ISO_DATE_TIME))));
  }

  static Stream<Arguments> invalidUserPatchRequest() {
    return Stream.of(
        Arguments.arguments(
            MockData.getUserPatchRequestDto().toBuilder()
                .employmentData(
                    MockData.getValidEmploymentDataDto().toBuilder().position("Opole-").build())
                .build()),
        Arguments.arguments(
            MockData.getUserPatchRequestDto().toBuilder()
                .employmentData(
                    MockData.getValidEmploymentDataDto().toBuilder().position("1").build())
                .build()),
        Arguments.arguments(
            MockData.getUserPatchRequestDto().toBuilder()
                .employmentData(
                    MockData.getValidEmploymentDataDto().toBuilder()
                        .position("A".repeat(51))
                        .build())
                .build()),
        Arguments.arguments(
            MockData.getUserPatchRequestDto().toBuilder()
                .employmentData(
                    MockData.getValidEmploymentDataDto().toBuilder().placeOfWork("Opole-").build())
                .build()),
        Arguments.arguments(
            MockData.getUserPatchRequestDto().toBuilder()
                .employmentData(
                    MockData.getValidEmploymentDataDto().toBuilder().placeOfWork("1").build())
                .build()),
        Arguments.arguments(
            MockData.getUserPatchRequestDto().toBuilder()
                .employmentData(
                    MockData.getValidEmploymentDataDto().toBuilder()
                        .placeOfWork("A".repeat(101))
                        .build())
                .build()));
  }
}
