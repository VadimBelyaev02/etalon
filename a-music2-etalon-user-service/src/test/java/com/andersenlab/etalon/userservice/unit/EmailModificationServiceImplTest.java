package com.andersenlab.etalon.userservice.unit;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.andersenlab.etalon.userservice.MockData;
import com.andersenlab.etalon.userservice.client.InfoServiceClient;
import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.ConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.confirmations.EmailModificationConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationInfoResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationResponseDto;
import com.andersenlab.etalon.userservice.entity.EmailModificationEntity;
import com.andersenlab.etalon.userservice.entity.UserEntity;
import com.andersenlab.etalon.userservice.exception.BusinessException;
import com.andersenlab.etalon.userservice.exception.NotFoundException;
import com.andersenlab.etalon.userservice.interceptor.AuthenticationHolder;
import com.andersenlab.etalon.userservice.repository.EmailModificationRepository;
import com.andersenlab.etalon.userservice.service.CognitoService;
import com.andersenlab.etalon.userservice.service.UserService;
import com.andersenlab.etalon.userservice.service.impl.EmailModificationServiceImpl;
import com.andersenlab.etalon.userservice.util.EmailModificationStatus;
import com.andersenlab.etalon.userservice.util.Operation;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class EmailModificationServiceImplTest {
  @Mock private EmailModificationRepository emailModificationRepository;

  @Mock private UserService userService;

  @Mock private AuthenticationHolder authenticationHolder;

  @Mock private InfoServiceClient infoServiceClient;

  @Mock private CognitoService cognitoService;

  @InjectMocks private EmailModificationServiceImpl emailModificationService;

  private EmailModificationEntity emailModificationEntity;
  private UserEntity user;
  private UserEmailModificationRequestDto requestDto;
  private ConfirmationResponseDto confirmationResponseDto;
  private CreateConfirmationResponseDto createConfirmationResponseDto;
  private EmailModificationConfirmationRequestDto confirmationRequest;

  @BeforeEach
  void setUp() {
    emailModificationEntity = MockData.getValidEmailModificationEntity();
    user = MockData.getValidUserEntity();
    requestDto = MockData.getValidEmailModificationRequestDto();
    confirmationResponseDto = MockData.getValidConfirmationResponseDto();
    createConfirmationResponseDto = MockData.getValidCreateConfirmationResponseDto();
    confirmationRequest = MockData.getValidEmailModificationConfirmationRequestDto();
  }

  @Test
  void testRequestEmailModification_FailureOnConfirmationCreation() {
    when(authenticationHolder.getUserId()).thenReturn("user");
    when(userService.getUserById("user")).thenReturn(user);
    when(emailModificationRepository.save(any())).thenReturn(emailModificationEntity);
    when(infoServiceClient.createConfirmation(any()))
        .thenThrow(new RuntimeException("Failed to create confirmation"));

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> emailModificationService.requestEmailModification(requestDto));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals("Failed to create confirmation", exception.getMessage());
  }

  @Test
  void testGetEmailModificationInfo_Success() {
    when(emailModificationRepository.findById(1L)).thenReturn(Optional.of(emailModificationEntity));

    EmailModificationInfoResponseDto response =
        emailModificationService.getEmailModificationInfo(1L);

    assertNotNull(response);
    assertEquals("user", response.userId());
    assertEquals("test@gmail.com", response.newEmail());
  }

  @Test
  void testRequestEmailModification_ResendConfirmation() {
    when(authenticationHolder.getUserId()).thenReturn("user");
    when(userService.getUserById("user")).thenReturn(user);
    when(emailModificationRepository.findEmailModificationEntitiesByUserIdAndNewEmail(
            "user", "test@gmail.com"))
        .thenReturn(Optional.of(List.of(emailModificationEntity)));
    when(infoServiceClient.getConfirmationsByOperationAndTargetId(Operation.EMAIL_MODIFICATION, 1L))
        .thenReturn(List.of(confirmationResponseDto));
    when(infoServiceClient.resendConfirmationCode(1L)).thenReturn(createConfirmationResponseDto);

    EmailModificationResponseDto response =
        emailModificationService.requestEmailModification(requestDto);

    assertNotNull(response);
    assertEquals(1L, response.confirmationId());
    verify(emailModificationRepository, never()).save(any());
  }

  @Test
  void testGetEmailModificationInfo_NotFound() {
    when(emailModificationRepository.findById(2L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> emailModificationService.getEmailModificationInfo(2L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    assertEquals("Modification entity not found by id 2", exception.getMessage());
  }

  @Test
  void testProcessEmailModification_Success() {
    when(emailModificationRepository.findById(1L)).thenReturn(Optional.of(emailModificationEntity));
    when(authenticationHolder.getUserId()).thenReturn("user");
    when(userService.getUserById("user")).thenReturn(user);

    MessageResponseDto response = emailModificationService.processEmailModification(1L);

    assertNotNull(response);
    assertEquals("An email has been updated for user with id user", response.message());
    assertEquals("test@gmail.com", user.getEmail());
    assertEquals(EmailModificationStatus.COMPLETED, emailModificationEntity.getStatus());
    verify(userService).updateUser(user);
    verify(cognitoService).updateUserEmail("user", "test@gmail.com");
    verify(emailModificationRepository).save(emailModificationEntity);
  }

  @Test
  void testProcessEmailModification_NotFound() {
    when(emailModificationRepository.findById(1L)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> emailModificationService.processEmailModification(1L));

    assertEquals("User email modification id(1) does not exist", exception.getMessage());
    verify(userService, never()).updateUser(any());
    verify(cognitoService, never()).updateUserEmail(anyString(), anyString());
    verify(emailModificationRepository, never()).save(any());
  }
}
