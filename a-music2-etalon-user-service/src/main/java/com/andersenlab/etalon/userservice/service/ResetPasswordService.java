package com.andersenlab.etalon.userservice.service;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.reset.confirmation.ResetPasswordConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.request.ResetPasswordRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.response.ResetPasswordResponseDto;

public interface ResetPasswordService {
  ResetPasswordResponseDto requestPasswordReset(ResetPasswordRequestDto resetPasswordRequestDto);

  MessageResponseDto confirmPasswordReset(
      ResetPasswordConfirmationRequestDto resetPasswordConfirmationRequestDto);
}
