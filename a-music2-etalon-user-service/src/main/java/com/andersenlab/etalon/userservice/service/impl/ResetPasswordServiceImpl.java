package com.andersenlab.etalon.userservice.service.impl;

import com.andersenlab.etalon.userservice.client.InfoServiceClient;
import com.andersenlab.etalon.userservice.config.TimeProvider;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.request.PasswordResetEmailRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.confirmation.ResetPasswordConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.request.ResetPasswordRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.response.ResetPasswordResponseDto;
import com.andersenlab.etalon.userservice.entity.ResetPasswordEntity;
import com.andersenlab.etalon.userservice.entity.UserEntity;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.exception.NotFoundException;
import com.andersenlab.etalon.userservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.userservice.repository.ResetPasswordRepository;
import com.andersenlab.etalon.userservice.repository.UserRepository;
import com.andersenlab.etalon.userservice.service.CognitoService;
import com.andersenlab.etalon.userservice.service.ResetPasswordService;
import com.andersenlab.etalon.userservice.util.EmailType;
import com.andersenlab.etalon.userservice.util.ResetPasswordStatus;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordServiceImpl implements ResetPasswordService {

  public static final String PASSWORD_RESET_SUCCESS = "Password has been successfully reset";
  public static final String PASSWORD_RESET_REQUEST_SUCCESS =
      "Password reset link has been sent to your email";
  public static final String RESET_TOKEN_EXPIRED = "Reset token has expired";
  public static final String RESET_TOKEN_INVALID = "Invalid or used reset token";
  public static final String USER_NOT_FOUND = "User with email: %s not found";

  private final ResetPasswordRepository resetPasswordRepository;
  private final UserRepository userRepository;
  private final CognitoService cognitoService;
  private final TimeProvider timeProvider;
  private final InfoServiceClient infoServiceClient;
  private final AuthenticationHolder authenticationHolder;

  @Value("${reset.password.token.expiration}")
  private long resetPasswordTokenExpirationMinutes;

  @Override
  @Transactional
  public ResetPasswordResponseDto requestPasswordReset(
      ResetPasswordRequestDto resetPasswordRequestDto) {
    String email = resetPasswordRequestDto.email();
    log.info("Processing password reset request for email: {}", email);

    String userId = getUserIdByEmail(email);
    authenticationHolder.setUserId(userId);

    UUID token = UUID.randomUUID();
    ZonedDateTime currentTime = timeProvider.getCurrentZonedDateTime();
    ZonedDateTime expiryTime = currentTime.plusMinutes(resetPasswordTokenExpirationMinutes);

    ResetPasswordEntity resetPasswordEntity =
        ResetPasswordEntity.builder()
            .userId(userId)
            .email(email)
            .token(token)
            .createdAt(currentTime)
            .expiresAt(expiryTime)
            .status(ResetPasswordStatus.PENDING)
            .build();

    resetPasswordEntity = resetPasswordRepository.save(resetPasswordEntity);

    PasswordResetEmailRequestDto emailRequest = new PasswordResetEmailRequestDto();
    emailRequest.setToEmail(email);
    emailRequest.setSubject("Password Reset Request");
    emailRequest.setType(EmailType.PASSWORD_RESET_CONFIRMATION);
    emailRequest.setResetToken(token.toString());

    try {
      infoServiceClient.sendEmail(emailRequest);

      resetPasswordEntity.setStatus(ResetPasswordStatus.CREATED);
      resetPasswordRepository.save(resetPasswordEntity);

      return ResetPasswordResponseDto.builder().message(PASSWORD_RESET_REQUEST_SUCCESS).build();
    } catch (Exception e) {
      log.error("Failed to create confirmation for password reset: {}", e.getMessage());
      throw new BusinessException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @Override
  @Transactional
  public MessageResponseDto confirmPasswordReset(ResetPasswordConfirmationRequestDto requestDto) {
    String tokenStr = requestDto.token();
    String newPassword = requestDto.newPassword();

    UUID token;
    try {
      token = UUID.fromString(tokenStr);
    } catch (IllegalArgumentException e) {
      log.warn("Invalid token format provided");
      throw new BusinessException(HttpStatus.BAD_REQUEST, RESET_TOKEN_INVALID);
    }
    ResetPasswordEntity resetPasswordEntity =
        resetPasswordRepository
            .findByTokenAndStatus(token, ResetPasswordStatus.CREATED)
            .orElseThrow(
                () -> {
                  log.warn("Valid token not found");
                  return new BusinessException(HttpStatus.BAD_REQUEST, RESET_TOKEN_INVALID);
                });

    ZonedDateTime currentTime = timeProvider.getCurrentZonedDateTime();
    if (currentTime.isAfter(resetPasswordEntity.getExpiresAt())) {
      resetPasswordEntity.setStatus(ResetPasswordStatus.EXPIRED);
      resetPasswordRepository.save(resetPasswordEntity);
      log.warn("Token expired");
      throw new BusinessException(HttpStatus.BAD_REQUEST, RESET_TOKEN_EXPIRED);
    }

    boolean isPasswordUpdated =
        cognitoService.updateUserPassword(resetPasswordEntity.getEmail(), newPassword);

    if (isPasswordUpdated) {
      resetPasswordEntity.setStatus(ResetPasswordStatus.COMPLETED);
      resetPasswordRepository.save(resetPasswordEntity);
      log.info("Password reset successful for user ID: {}", resetPasswordEntity.getUserId());
      return new MessageResponseDto(PASSWORD_RESET_SUCCESS);
    } else {
      log.error(
          "Failed to reset password in Cognito for user ID: {}", resetPasswordEntity.getUserId());
      throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to reset password");
    }
  }

  private String getUserIdByEmail(String email) {
    UserEntity user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, email)));
    return user.getId();
  }
}
