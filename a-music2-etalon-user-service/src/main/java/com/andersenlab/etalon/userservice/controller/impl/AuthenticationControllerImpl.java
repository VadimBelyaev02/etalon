package com.andersenlab.etalon.userservice.controller.impl;

import com.andersenlab.etalon.userservice.controller.AuthenticationController;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import com.andersenlab.etalon.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {
  private final UserService userService;

  @GetMapping(USER_AUTH_URL)
  public UserDataResponseDto getUserInfo(
      @Parameter(hidden = true) @RequestHeader(value = "authenticated-user-id") String userId) {
    return userService.getUserDataById(userId);
  }
}
