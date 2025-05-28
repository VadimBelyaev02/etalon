package com.andersenlab.etalon.userservice.controller.impl;

import com.andersenlab.etalon.userservice.controller.RegistrationController;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.InitiateRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.InitiateRegistrationResponseDto;
import com.andersenlab.etalon.userservice.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RegistrationController.REGISTRATIONS_URL)
@Slf4j
@RequiredArgsConstructor
public class RegistrationControllerImpl implements RegistrationController {

  private final UserService userService;

  @PostMapping(COGNITO)
  @Override
  public InitiateRegistrationResponseDto initiateRegistration(
      @RequestBody @Valid InitiateRegistrationRequestDto dto) {
    return userService.initiateRegistration(dto);
  }

  @PostMapping(COMPLETE_REGISTRATION_ID)
  @Override
  public MessageResponseDto completeRegistration(
      @PathVariable("registrationId") final UUID registrationId,
      @RequestBody @Valid CompleteRegistrationRequestDto registrationDto) {
    return userService.registerUserInCognito(registrationId, registrationDto);
  }
}
