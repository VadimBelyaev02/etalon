package com.andersenlab.etalon.infoservice.client;

import com.andersenlab.etalon.infoservice.config.FeignConfig;
import com.andersenlab.etalon.infoservice.dto.request.PeselRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.EmailModificationInfoResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.PersonalInfoDto;
import com.andersenlab.etalon.infoservice.dto.response.StatusMessageResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserDataResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "user-service",
    url = "${feign.user-service.url}",
    configuration = FeignConfig.class,
    path = "/user")
public interface UserServiceClient {

  String API_V1_URI = "/api/v1";
  String USERS_URI = "/users";
  String DETAILS_URI = "/details";
  String USER_ID_PATH = "/{userId}";
  String USER_DETAILS_URL = API_V1_URI + USERS_URI + USER_ID_PATH + DETAILS_URI;
  String REGISTRATIONS_URI = "/registrations";
  String USER_REGISTRATIONS_URL = API_V1_URI + REGISTRATIONS_URI;
  String PESELS_URI = "/user-pesels";
  String VALIDATED_URI = "/validated";
  String CONFIRM = "/confirm";
  String VALIDATED_PESEL_BEFORE_REGISTRATION_URL = API_V1_URI + PESELS_URI + VALIDATED_URI;
  String INTERNAL_URI = "/internal";
  String INTERNAL_API_PROCESS_USER_REGISTRATION_URL =
      INTERNAL_URI + API_V1_URI + REGISTRATIONS_URI + CONFIRM;
  String EMAIL_MODIFICATION_URI = "/email-modification";
  String EMAIL_MODIFICATION_ID_PATH = "/{modificationId}";
  String TARGET_ID_PATH = "/{targetId}";
  String INTERNAL_API_PROCESS_EMAIL_MODIFICATION_URI =
      INTERNAL_URI + API_V1_URI + EMAIL_MODIFICATION_URI + TARGET_ID_PATH;
  String INTERNAL_API_EMAIL_MODIFICATION_INFO_URI =
      INTERNAL_URI + API_V1_URI + EMAIL_MODIFICATION_URI + EMAIL_MODIFICATION_ID_PATH;
  String INTERNAL_API_GET_USER_DATA_BY_REGISTRATION_ID_URI =
      INTERNAL_URI + API_V1_URI + USERS_URI + TARGET_ID_PATH;

  @GetMapping(USER_DETAILS_URL)
  UserDataResponseDto getUserData(@PathVariable("userId") final String userId);

  @PostMapping(USER_REGISTRATIONS_URL)
  MessageResponseDto registerUser(@RequestBody final UserResponseDto userDto);

  @PostMapping(VALIDATED_PESEL_BEFORE_REGISTRATION_URL)
  StatusMessageResponseDto validatePeselBeforeRegistration(@RequestBody final PeselRequestDto dto);

  @PutMapping(INTERNAL_API_PROCESS_USER_REGISTRATION_URL)
  PersonalInfoDto processUserRegistration(@RequestBody Long targetId);

  @GetMapping(INTERNAL_API_EMAIL_MODIFICATION_INFO_URI)
  EmailModificationInfoResponseDto getEmailModificationInfo(@PathVariable long modificationId);

  @PostMapping(INTERNAL_API_PROCESS_EMAIL_MODIFICATION_URI)
  MessageResponseDto processEmailModification(@PathVariable long targetId);

  @GetMapping(INTERNAL_API_GET_USER_DATA_BY_REGISTRATION_ID_URI)
  UserDataResponseDto getUserDataById(@PathVariable Long targetId);
}
