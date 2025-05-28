package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.dto.request.ConfirmationEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.request.CreateConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.ConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationPostProcessorService;
import com.andersenlab.etalon.infoservice.service.EmailService;
import com.andersenlab.etalon.infoservice.service.chain.validation.CreateConfirmationValidationChain;
import com.andersenlab.etalon.infoservice.service.impl.ConfirmationServiceImpl;
import com.andersenlab.etalon.infoservice.service.strategy.emailrequestpreparation.EmailRequestPreparationContext;
import com.andersenlab.etalon.infoservice.util.ConfirmationCodeGenerator;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationMethod;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConfirmationServiceImplTest {
  private static final String VALIDATION_FAILED_ERROR = "Validation failed";
  private static final long CONFIRMATION_ID = 123L;
  private static final long TARGET_ID = 1L;

  @Mock private ConfirmationRepository confirmationRepository;

  @Mock private CreateConfirmationValidationChain createConfirmationValidationChain;

  @Mock private ConfirmationPostProcessorService confirmationPostProcessorService;

  @Mock private EmailService emailService;

  @Mock private EmailRequestPreparationContext emailRequestPreparationContext;

  @InjectMocks private ConfirmationServiceImpl confirmationService;

  @Test
  void testProcessConfirmation_Success() {
    ConfirmationEntity mockConfirmation = MockData.getValidConfirmationEntity();

    ConfirmationEntity updatedConfirmation =
        mockConfirmation.toBuilder().status(ConfirmationStatus.CONFIRMED).build();

    ConfirmationEmailRequestDto mockEmailRequest = new ConfirmationEmailRequestDto();

    try (MockedStatic<ConfirmationCodeGenerator> mockedStatic =
        mockStatic(ConfirmationCodeGenerator.class)) {
      mockedStatic.when(ConfirmationCodeGenerator::generateConfirmationCode).thenReturn("123456");

      when(confirmationRepository.findById(CONFIRMATION_ID))
          .thenReturn(Optional.of(mockConfirmation));
      when(confirmationRepository.save(any(ConfirmationEntity.class)))
          .thenReturn(updatedConfirmation);
      when(emailRequestPreparationContext.prepareEmail(any(ConfirmationEntity.class)))
          .thenReturn(mockEmailRequest);
      doNothing().when(emailService).sendEmail(any(ConfirmationEmailRequestDto.class));

      confirmationService.processConfirmation(CONFIRMATION_ID);
    }
  }

  @Test
  void testProcessConfirmation_ConfirmationNotFound() {

    when(confirmationRepository.findById(CONFIRMATION_ID)).thenReturn(Optional.empty());

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              confirmationService.processConfirmation(CONFIRMATION_ID);
            });

    assertEquals("Confirmation request with id 123 is not found", exception.getMessage());

    verify(confirmationRepository, times(1)).findById(CONFIRMATION_ID);
    verifyNoMoreInteractions(confirmationRepository, emailRequestPreparationContext, emailService);
  }

  @Test
  void testCreateConfirmation_Success() {
    CreateConfirmationRequestDto requestDto = MockData.getValidConfirmationRequestRequestDto();

    when(createConfirmationValidationChain.validate(any())).thenReturn(true);

    ConfirmationEntity savedEntity =
        ConfirmationEntity.builder()
            .id(1L)
            .confirmationCode(512361)
            .confirmationMethod(ConfirmationMethod.EMAIL)
            .operation(Operation.OPEN_DEPOSIT)
            .targetId(123L)
            .status(ConfirmationStatus.CREATED)
            .build();
    when(confirmationRepository.save(any(ConfirmationEntity.class))).thenReturn(savedEntity);

    CreateConfirmationResponseDto response = confirmationService.createConfirmation(requestDto);

    assertNotNull(response);
    assertEquals(1L, response.confirmationId());
    assertEquals(ConfirmationMethod.EMAIL, response.confirmationMethod());
  }

  @Test
  void testCreateConfirmation_ValidationFails() {
    CreateConfirmationRequestDto requestDto = MockData.getValidConfirmationRequestRequestDto();

    doThrow(new IllegalArgumentException(VALIDATION_FAILED_ERROR))
        .when(createConfirmationValidationChain)
        .validate(any());

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> confirmationService.createConfirmation(requestDto));

    assertEquals(VALIDATION_FAILED_ERROR, exception.getMessage());
  }

  @Test
  void testGetConfirmationsByOperationAndTargetId_Success() {
    ConfirmationEntity validConfirmation = MockData.getValidConfirmationEntity();

    List<ConfirmationEntity> mockEntities = List.of(validConfirmation);

    given(
            confirmationRepository.findByTargetIdAndOperation(
                TARGET_ID, Operation.EMAIL_MODIFICATION))
        .willReturn(mockEntities);

    List<ConfirmationResponseDto> result =
        confirmationService.getConfirmationsByOperationAndTargetId(
            Operation.EMAIL_MODIFICATION, TARGET_ID);

    assertNotNull(result);
    assertEquals(1, result.size());

    assertEquals(1L, result.get(0).confirmationId());
    assertEquals(ConfirmationStatus.CREATED, result.get(0).confirmationStatus());

    verify(confirmationRepository, times(1))
        .findByTargetIdAndOperation(TARGET_ID, Operation.EMAIL_MODIFICATION);
  }

  @Test
  void testGetConfirmationsByOperationAndTargetId_EmptyResult() {
    given(
            confirmationRepository.findByTargetIdAndOperation(
                TARGET_ID, Operation.EMAIL_MODIFICATION))
        .willReturn(Collections.emptyList());

    List<ConfirmationResponseDto> result =
        confirmationService.getConfirmationsByOperationAndTargetId(
            Operation.EMAIL_MODIFICATION, TARGET_ID);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(confirmationRepository, times(1))
        .findByTargetIdAndOperation(TARGET_ID, Operation.EMAIL_MODIFICATION);
  }

  @Test
  void testGetConfirmationsByOperationAndTargetId_NullTargetId() {
    assertEquals(
        confirmationService.getConfirmationsByOperationAndTargetId(
            Operation.EMAIL_MODIFICATION, null),
        Collections.emptyList());
  }

  @Test
  void testGetConfirmationsByOperationAndTargetId_NullOperation() {
    assertEquals(
        confirmationService.getConfirmationsByOperationAndTargetId(null, TARGET_ID),
        Collections.emptyList());
  }
}
