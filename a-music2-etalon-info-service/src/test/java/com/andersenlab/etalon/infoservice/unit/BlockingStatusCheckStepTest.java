package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.ConfirmationBlockedException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.BlockingStatusCheckStep;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class BlockingStatusCheckStepTest {
  private static final String CONFIRMATION_BLOCKED_MESSAGE = "Confirmation is blocked";

  @Mock private ConfirmationRepository confirmationRepository;

  @InjectMocks private BlockingStatusCheckStep blockingStatusCheckStep;

  @Test
  void validate_Success_WhenNotBlocked() {
    ConfirmationEntity confirmationEntity = MockData.getConfirmationEntityWithBlockedStatus();

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    boolean result = blockingStatusCheckStep.validate(validationData);

    assertTrue(result);
  }

  @Test
  void validate_ThrowsException_WhenStillBlocked() {
    ZonedDateTime futureTime = ZonedDateTime.now().plusMinutes(10);

    ConfirmationEntity confirmationEntity = MockData.getConfirmationEntityWithBlockedStatus();
    confirmationEntity.setBlockedUntil(futureTime);

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    ConfirmationBlockedException exception =
        assertThrows(
            ConfirmationBlockedException.class,
            () -> blockingStatusCheckStep.validate(validationData));

    assertEquals(HttpStatus.LOCKED, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains(CONFIRMATION_BLOCKED_MESSAGE));

    verifyNoInteractions(confirmationRepository);
  }
}
