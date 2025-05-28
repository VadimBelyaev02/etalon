package com.andersenlab.etalon.userservice.controller.impl;

import com.andersenlab.etalon.userservice.controller.UserDataController;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationResponseDto;
import com.andersenlab.etalon.userservice.dto.reset.confirmation.ResetPasswordConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.request.ResetPasswordRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.response.ResetPasswordResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserPatchRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.userservice.service.EmailModificationService;
import com.andersenlab.etalon.userservice.service.ResetPasswordService;
import com.andersenlab.etalon.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserDataController.USERS_URL)
@Slf4j
@RequiredArgsConstructor
public class UserDataControllerImpl implements UserDataController {

  private final UserService userService;
  private final AuthenticationHolder authenticationHolder;
  private final EmailModificationService emailModificationService;
  private final ResetPasswordService resetPasswordService;

  @GetMapping(USER_ID_PATH + DETAILS_URI)
  public UserDataResponseDto getUserData(@PathVariable("userId") final String userId) {
    return userService.getUserDataById(userId);
  }

  @GetMapping(USER_DETAILS_BY_PHONE_NUMBER_URL)
  public UserDataResponseDto getUserDataByPhoneNumber(
      @PathVariable("phoneNumber") final String phoneNumber) {
    return userService.getUserDataByPhoneNumber(phoneNumber);
  }

  @PatchMapping
  public MessageResponseDto updateUserData(@RequestBody final UserPatchRequestDto userRequestDto) {
    userService.patchUserById(userRequestDto, authenticationHolder.getUserId());
    return new MessageResponseDto(
        String.format("User with id: %s updated", authenticationHolder.getUserId()));
  }

  @PostMapping(USER_EMAIL_MODIFICATION)
  public EmailModificationResponseDto requestEmailModification(
      @RequestBody final UserEmailModificationRequestDto userEmailModificationRequestDto) {
    return emailModificationService.requestEmailModification(userEmailModificationRequestDto);
  }

  @PostMapping(RESET_PASSWORD_LINK_URL)
  public ResetPasswordResponseDto requestResetPasswordLink(
      @RequestBody final ResetPasswordRequestDto resetPasswordRequestDto) {
    return resetPasswordService.requestPasswordReset(resetPasswordRequestDto);
  }

  @PostMapping(RESET_PASSWORD_CONFIRM_URL)
  public MessageResponseDto confirmResetPassword(
      @RequestBody final ResetPasswordConfirmationRequestDto resetPasswordConfirmationRequestDto) {
    return resetPasswordService.confirmPasswordReset(resetPasswordConfirmationRequestDto);
  }
}
