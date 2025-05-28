package com.andersenlab.etalon.userservice.service.impl;

import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserRequestDto;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.repository.UserRepository;
import com.andersenlab.etalon.userservice.service.ValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidatorServiceImpl implements ValidatorService {
  private static final String EMAIL_IS_AVAILABLE = "Email is available to register";
  private static final String PESEL_IS_AVAILABLE = "Pesel is available to register";
  private static final String PHONE_NUMBER_IS_AVAILABLE = "Phone number is available to register";

  private final UserRepository userRepository;

  @Override
  public void validateUserRequestDto(UserRequestDto userRequestDto) {
    isPeselAvailableToRegister(userRequestDto.pesel());
    isEmailAvailableToRegister(userRequestDto.email());
  }

  @Override
  public StatusMessageResponseDto isPhoneNumberAvailableToRegister(final String phoneNumber) {

    if (userRepository.existsByPhoneNumber(phoneNumber)) {
      throw new BusinessException(
          HttpStatus.CONFLICT, BusinessException.PHONE_NUMBER_IS_NOT_AVAILABLE);
    }
    return new StatusMessageResponseDto(true, PHONE_NUMBER_IS_AVAILABLE);
  }

  @Override
  public StatusMessageResponseDto isEmailAvailableToRegister(final String email) {

    if (userRepository.existsByEmail(email)) {
      throw new BusinessException(HttpStatus.CONFLICT, BusinessException.EMAIL_IS_NOT_AVAILABLE);
    }
    return new StatusMessageResponseDto(true, EMAIL_IS_AVAILABLE);
  }

  @Override
  public StatusMessageResponseDto isPeselAvailableToRegister(final String pesel) {
    if (userRepository.existsByPesel(pesel)) {
      throw new BusinessException(HttpStatus.CONFLICT, BusinessException.PESEL_IS_NOT_AVAILABLE);
    }
    return new StatusMessageResponseDto(true, PESEL_IS_AVAILABLE);
  }
}
