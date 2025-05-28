package com.andersenlab.etalon.userservice.controller.internal;

import static com.andersenlab.etalon.userservice.controller.RegistrationController.CONFIRM;
import static com.andersenlab.etalon.userservice.controller.RegistrationController.REGISTRATIONS_URI;
import static com.andersenlab.etalon.userservice.controller.RegistrationController.REGISTRATIONS_URL;
import static com.andersenlab.etalon.userservice.controller.UserDataController.USERS_URI;

import com.andersenlab.etalon.userservice.dto.user.request.UpdateRegistrationOrderStatusRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.PersonalInfoDto;
import com.andersenlab.etalon.userservice.dto.user.response.RegistrationOrderResponseDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.service.RegistrationOrderService;
import com.andersenlab.etalon.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(InternalRegistrationController.URI)
public class InternalRegistrationController {
  public static final String URI = "/internal/api/v1";
  public static final String TARGET_ID_PATH = "/{targetId}";
  public static final String INTERNAL_API_GET_USER_DATA_BY_REGISTRATION_ID_URI =
      USERS_URI + TARGET_ID_PATH;
  public static final String INTERNAL_API_PROCESS_USER_REGISTRATION_URL =
      REGISTRATIONS_URI + CONFIRM;
  private final RegistrationOrderService registrationOrderService;
  private final UserService userService;

  @PutMapping(REGISTRATIONS_URL)
  RegistrationOrderResponseDto updateRegistrationOrderStatus(
      @RequestBody
          UpdateRegistrationOrderStatusRequestDto updateRegistrationOrderStatusRequestDto) {
    return registrationOrderService.updateRegistrationOrderStatus(
        updateRegistrationOrderStatusRequestDto);
  }

  @GetMapping(INTERNAL_API_GET_USER_DATA_BY_REGISTRATION_ID_URI)
  UserDataResponseDto getUserDataById(@PathVariable Long targetId) {
    return userService.getUserDataById(targetId);
  }

  @PutMapping(INTERNAL_API_PROCESS_USER_REGISTRATION_URL)
  PersonalInfoDto processUserRegistration(@RequestBody Long targetId) {
    return userService.processUserRegistration(targetId);
  }
}
