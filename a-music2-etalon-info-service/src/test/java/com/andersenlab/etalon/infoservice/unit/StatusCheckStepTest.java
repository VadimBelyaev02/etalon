package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.exception.ConfirmationBlockedException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.StatusCheckStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class StatusCheckStepTest {
  private static final String CONFIRMATION_BLOCKED_MESSAGE = "Confirmation is blocked";
  private static final String OPERATION_ALREADY_PROCESSED = "Operation has already been processed.";

  @Mock private ConfirmationRepository confirmationRepository;

  @InjectMocks private StatusCheckStep statusCheckStep;

  @Test
  void validate_Success_WhenStatusIsCreated() {
    ConfirmationEntity confirmationEntity = MockData.getValidConfirmationEntity();

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    boolean result = statusCheckStep.validate(validationData);

    assertTrue(result);
  }

  @Test
  void validate_Success_WhenBlockExpired() {
    ZonedDateTime pastTime = ZonedDateTime.now().minusMinutes(1);

    ConfirmationEntity confirmationEntity = MockData.getConfirmationEntityWithBlockedStatus();
    confirmationEntity.setBlockedUntil(pastTime);

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    boolean result = statusCheckStep.validate(validationData);

    assertTrue(result);
    assertEquals(ConfirmationStatus.CREATED, confirmationEntity.getStatus());
    verify(confirmationRepository).save(confirmationEntity);
  }

  @Test
  void validate_ThrowsException_WhenStillBlocked() {
    ZonedDateTime futureTime = ZonedDateTime.now().plusMinutes(10);

    ConfirmationEntity confirmationEntity = MockData.getConfirmationEntityWithBlockedStatus();
    confirmationEntity.setBlockedUntil(futureTime);

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    ConfirmationBlockedException exception =
        assertThrows(
            ConfirmationBlockedException.class, () -> statusCheckStep.validate(validationData));

    assertEquals(HttpStatus.LOCKED, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains(CONFIRMATION_BLOCKED_MESSAGE));

    verifyNoInteractions(confirmationRepository);
  }

  @Test
  void validate_ThrowsException_WhenStatusIsConfirmed() {
    ConfirmationEntity confirmationEntity = MockData.getValidConfirmationEntity();
    confirmationEntity.setStatus(ConfirmationStatus.CONFIRMED);

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> statusCheckStep.validate(validationData));

    assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    assertEquals(OPERATION_ALREADY_PROCESSED, exception.getMessage());

    verifyNoInteractions(confirmationRepository);
  }
}
