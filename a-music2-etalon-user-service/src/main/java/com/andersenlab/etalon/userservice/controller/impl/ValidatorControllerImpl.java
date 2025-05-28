package com.andersenlab.etalon.userservice.controller.impl;

import static com.andersenlab.etalon.userservice.controller.ValidatorController.API_V1_URI;

import com.andersenlab.etalon.userservice.controller.ValidatorController;
import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.EmailDto;
import com.andersenlab.etalon.userservice.dto.user.request.PeselDto;
import com.andersenlab.etalon.userservice.service.ValidatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_V1_URI)
@Slf4j
@RequiredArgsConstructor
public class ValidatorControllerImpl implements ValidatorController {

  private final ValidatorService validatorService;

  @PostMapping(VALIDATED_EMAIL_BEFORE_REGISTRATION_URI)
  public StatusMessageResponseDto validateEmailBeforeRegistration(
      @RequestBody @Valid final EmailDto emailDto) {
    return validatorService.isEmailAvailableToRegister(emailDto.email());
  }

  @PostMapping(VALIDATED_PESEL_BEFORE_REGISTRATION_URI)
  public StatusMessageResponseDto validatePeselBeforeRegistration(
      @RequestBody @Valid final PeselDto dto) {
    return validatorService.isPeselAvailableToRegister(dto.pesel());
  }
}
