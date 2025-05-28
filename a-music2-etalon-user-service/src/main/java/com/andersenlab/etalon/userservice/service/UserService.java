package com.andersenlab.etalon.userservice.service;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.InitiateRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserPatchRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.InitiateRegistrationResponseDto;
import com.andersenlab.etalon.userservice.dto.user.response.PersonalInfoDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.entity.UserEntity;
import jakarta.validation.Valid;
import java.util.UUID;

public interface UserService {

  UserDataResponseDto getUserDataById(final String userId);

  UserDataResponseDto patchUserById(final UserPatchRequestDto patchRequestDto, final String userId);

  UserDataResponseDto getUserDataByPhoneNumber(final String phoneNumber);

  InitiateRegistrationResponseDto initiateRegistration(InitiateRegistrationRequestDto dto);

  UserEntity getUserById(String userId);

  UserEntity updateUser(UserEntity user);

  boolean existsByEmail(final String email);

  MessageResponseDto registerUserInCognito(
      UUID registrationId, @Valid CompleteRegistrationRequestDto registrationDto);

  UserDataResponseDto getUserDataById(Long targetId);

  PersonalInfoDto processUserRegistration(Long targetId);
}
