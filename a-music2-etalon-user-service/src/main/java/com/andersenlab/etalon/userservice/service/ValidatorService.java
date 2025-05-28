package com.andersenlab.etalon.userservice.service;

import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserRequestDto;

public interface ValidatorService {

  void validateUserRequestDto(UserRequestDto userRequestDto);

  StatusMessageResponseDto isPhoneNumberAvailableToRegister(String phoneNumber);

  StatusMessageResponseDto isEmailAvailableToRegister(final String email);

  StatusMessageResponseDto isPeselAvailableToRegister(final String pesel);
}
