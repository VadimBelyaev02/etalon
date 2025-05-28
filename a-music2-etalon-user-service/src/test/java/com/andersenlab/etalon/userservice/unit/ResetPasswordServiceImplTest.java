package com.andersenlab.etalon.userservice.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.andersenlab.etalon.userservice.client.InfoServiceClient;
import com.andersenlab.etalon.userservice.config.TimeProvider;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.request.BaseEmailRequestDto;
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
import com.andersenlab.etalon.userservice.service.impl.ResetPasswordServiceImpl;
import com.andersenlab.etalon.userservice.util.ResetPasswordStatus;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceImplTest {
  @Mock private ResetPasswordRepository resetPasswordRepository;

  @Mock private UserRepository userRepository;

  @Mock private CognitoService cognitoService;

  @Mock private TimeProvider timeProvider;

  @Mock private InfoServiceClient infoServiceClient;

  @InjectMocks private ResetPasswordServiceImpl resetPasswordService;

  @Mock private AuthenticationHolder authenticationHolder;

  private ResetPasswordRequestDto resetPasswordRequestDto;
  private ResetPasswordConfirmationRequestDto confirmationRequestDto;
  private UserEntity userEntity;
  private ResetPasswordEntity resetPasswordEntity;
  private ZonedDateTime currentTime;
  private ZonedDateTime expiryTime;
  private UUID resetToken;

  @BeforeEach
  void setUp() {
    resetPasswordRequestDto = new ResetPasswordRequestDto("test@example.com");
    confirmationRequestDto =
        new ResetPasswordConfirmationRequestDto(
            "123e4567-e89b-12d3-a456-426614174000", "NewPassword123");

    userEntity = new UserEntity();
    userEntity.setId("user123");
    userEntity.setEmail("test@example.com");

    resetToken = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    currentTime = ZonedDateTime.now();
    expiryTime = currentTime.plusMinutes(60);

    resetPasswordEntity =
        ResetPasswordEntity.builder()
            .resetPasswordId(1L)
            .userId("user123")
            .email("test@example.com")
            .token(resetToken)
            .createdAt(currentTime)
            .expiresAt(expiryTime)
            .status(ResetPasswordStatus.CREATED)
            .build();
  }

  @Test
  void requestPasswordReset_Success() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentTime);
    when(resetPasswordRepository.save(any(ResetPasswordEntity.class)))
        .thenReturn(resetPasswordEntity);

    ResetPasswordResponseDto response =
        resetPasswordService.requestPasswordReset(resetPasswordRequestDto);

    assertNotNull(response);
    assertEquals(ResetPasswordServiceImpl.PASSWORD_RESET_REQUEST_SUCCESS, response.message());

    ArgumentCaptor<ResetPasswordEntity> entityCaptor =
        ArgumentCaptor.forClass(ResetPasswordEntity.class);
    verify(resetPasswordRepository, times(2)).save(entityCaptor.capture());

    ResetPasswordEntity capturedEntity = entityCaptor.getAllValues().get(0);
    assertEquals("user123", capturedEntity.getUserId());
    assertEquals("test@example.com", capturedEntity.getEmail());
    assertEquals(ResetPasswordStatus.PENDING, capturedEntity.getStatus());

    verify(authenticationHolder).setUserId("user123");
    verify(infoServiceClient).sendEmail(any(BaseEmailRequestDto.class));
  }

  @Test
  void requestPasswordReset_UserNotFound() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class,
            () -> resetPasswordService.requestPasswordReset(resetPasswordRequestDto));

    assertEquals(
        String.format(ResetPasswordServiceImpl.USER_NOT_FOUND, "test@example.com"),
        exception.getMessage());
    verify(resetPasswordRepository, never()).save(any());
    verify(infoServiceClient, never()).createConfirmation(any());
  }

  @Test
  void requestPasswordReset_ConfirmationCreationFails() {
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentTime);
    when(resetPasswordRepository.save(any(ResetPasswordEntity.class)))
        .thenReturn(resetPasswordEntity);
    doThrow(new RuntimeException("Service unavailable")).when(infoServiceClient).sendEmail(any());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> resetPasswordService.requestPasswordReset(resetPasswordRequestDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals("Service unavailable", exception.getMessage());

    verify(authenticationHolder).setUserId("user123");
  }

  @Test
  void confirmPasswordReset_Success() {

    when(resetPasswordRepository.findByTokenAndStatus(resetToken, ResetPasswordStatus.CREATED))
        .thenReturn(Optional.of(resetPasswordEntity));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentTime);
    when(cognitoService.updateUserPassword("test@example.com", "NewPassword123")).thenReturn(true);

    MessageResponseDto response = resetPasswordService.confirmPasswordReset(confirmationRequestDto);

    assertNotNull(response);
    assertEquals(ResetPasswordServiceImpl.PASSWORD_RESET_SUCCESS, response.message());

    verify(resetPasswordRepository).save(resetPasswordEntity);
    assertEquals(ResetPasswordStatus.COMPLETED, resetPasswordEntity.getStatus());
  }

  @Test
  void confirmPasswordReset_TokenNotFound() {
    when(resetPasswordRepository.findByTokenAndStatus(resetToken, ResetPasswordStatus.CREATED))
        .thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> resetPasswordService.confirmPasswordReset(confirmationRequestDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(ResetPasswordServiceImpl.RESET_TOKEN_INVALID, exception.getMessage());
    verify(cognitoService, never()).updateUserPassword(anyString(), anyString());
  }

  @Test
  void confirmPasswordReset_ExpiredToken() {
    when(resetPasswordRepository.findByTokenAndStatus(resetToken, ResetPasswordStatus.CREATED))
        .thenReturn(Optional.of(resetPasswordEntity));

    ZonedDateTime futureTime = expiryTime.plusMinutes(10);
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(futureTime);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> resetPasswordService.confirmPasswordReset(confirmationRequestDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(ResetPasswordServiceImpl.RESET_TOKEN_EXPIRED, exception.getMessage());

    verify(resetPasswordRepository).save(resetPasswordEntity);
    assertEquals(ResetPasswordStatus.EXPIRED, resetPasswordEntity.getStatus());
    verify(cognitoService, never()).updateUserPassword(anyString(), anyString());
  }

  @Test
  void confirmPasswordReset_InvalidTokenFormat() {
    ResetPasswordConfirmationRequestDto invalidRequest =
        new ResetPasswordConfirmationRequestDto("not-a-uuid", "NewPassword123");

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> resetPasswordService.confirmPasswordReset(invalidRequest));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(ResetPasswordServiceImpl.RESET_TOKEN_INVALID, exception.getMessage());
    verify(resetPasswordRepository, never()).findByTokenAndStatus(any(), any());
  }

  @Test
  void confirmPasswordReset_CognitoFailure() {
    when(resetPasswordRepository.findByTokenAndStatus(resetToken, ResetPasswordStatus.CREATED))
        .thenReturn(Optional.of(resetPasswordEntity));
    when(timeProvider.getCurrentZonedDateTime()).thenReturn(currentTime);
    when(cognitoService.updateUserPassword("test@example.com", "NewPassword123")).thenReturn(false);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> resetPasswordService.confirmPasswordReset(confirmationRequestDto));

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    assertEquals("Failed to reset password", exception.getMessage());
    verify(resetPasswordRepository, never()).save(any());
  }
}
