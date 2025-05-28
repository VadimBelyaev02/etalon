package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.exception.ConfirmationBlockedException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.TargetIdStatusCheckStep;
import com.andersenlab.etalon.infoservice.util.enums.Operation;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TargetIdStatusCheckStepTest {
  private static final String CONFIRMATION_BLOCKED_MESSAGE = "Confirmation is blocked";
  private static final String CONFIRMATION_REJECTED_MESSAGE =
      "Confirmation with id-%s is in rejected status";
  private static final String CONFIRMATION_ALREADY_CONFIRMED_MESSAGE =
      "Confirmation with id-%s is already confirmed";
  private static final String CONFIRMATION_ALREADY_CREATED_MESSAGE =
      "<CREATED> Confirmation is already created, check email for confirmation code sent";

  @Mock private ConfirmationRepository confirmationRepository;

  @InjectMocks private TargetIdStatusCheckStep targetIdStatusCheckStep;

  private static final Long TARGET_ID = 5L;
  private static final Operation OPERATION = Operation.EMAIL_MODIFICATION;

  @Test
  void validate_Success_WhenNoPreviousConfirmation() {
    ConfirmationEntity confirmationEntity = MockData.getValidConfirmationEntity();
    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    when(confirmationRepository.findByTargetIdAndOperation(TARGET_ID, OPERATION))
        .thenReturn(Collections.emptyList());

    boolean result = targetIdStatusCheckStep.validate(validationData);

    assertTrue(result);
    verify(confirmationRepository, times(1)).findByTargetIdAndOperation(TARGET_ID, OPERATION);
  }

  @Test
  void validate_ThrowsException_WhenLatestConfirmationIsBlocked() {
    ConfirmationEntity blockedConfirmation = MockData.getConfirmationEntityWithBlockedStatus();
    blockedConfirmation.setBlockedUntil(ZonedDateTime.now().plusMinutes(10));

    ValidationData validationData = new ValidationData(blockedConfirmation, 123456);

    when(confirmationRepository.findByTargetIdAndOperation(TARGET_ID, OPERATION))
        .thenReturn(List.of(blockedConfirmation));

    ConfirmationBlockedException exception =
        assertThrows(
            ConfirmationBlockedException.class,
            () -> targetIdStatusCheckStep.validate(validationData));

    assertEquals(HttpStatus.LOCKED, exception.getHttpStatus());
    assertEquals(
        CONFIRMATION_BLOCKED_MESSAGE.formatted(blockedConfirmation.getBlockedUntil()),
        exception.getMessage());
  }

  @Test
  void validate_ThrowsException_WhenLatestConfirmationIsRejected() {
    ConfirmationEntity rejectedConfirmation = MockData.getConfirmationEntityWithRejectedStatus();

    ValidationData validationData = new ValidationData(rejectedConfirmation, 123456);

    when(confirmationRepository.findByTargetIdAndOperation(TARGET_ID, OPERATION))
        .thenReturn(List.of(rejectedConfirmation));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> targetIdStatusCheckStep.validate(validationData));

    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    assertEquals(
        CONFIRMATION_REJECTED_MESSAGE.formatted(rejectedConfirmation.getId()),
        exception.getMessage());
  }

  @Test
  void validate_ThrowsException_WhenLatestConfirmationIsConfirmed() {
    ConfirmationEntity confirmedConfirmation = MockData.getConfirmationEntityWithConfirmedStatus();

    ValidationData validationData = new ValidationData(confirmedConfirmation, 123456);

    when(confirmationRepository.findByTargetIdAndOperation(TARGET_ID, OPERATION))
        .thenReturn(List.of(confirmedConfirmation));

    assertTrue(targetIdStatusCheckStep.validate(validationData));
  }

  @Test
  void validate_ThrowsException_WhenLatestConfirmationIsCreated() {
    ConfirmationEntity createdConfirmation = MockData.getValidConfirmationEntity();

    ValidationData validationData = new ValidationData(createdConfirmation, 123456);

    when(confirmationRepository.findByTargetIdAndOperation(TARGET_ID, OPERATION))
        .thenReturn(List.of(createdConfirmation));

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> targetIdStatusCheckStep.validate(validationData));

    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    assertEquals(CONFIRMATION_ALREADY_CREATED_MESSAGE, exception.getMessage());
  }
}
