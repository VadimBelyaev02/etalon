package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.CodeValidationStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class CodeValidationStepTest {
  private static final String TWO_ATTEMPTS_LEFT = "You have entered invalid code. 2 attempts left.";
  private static final String NO_ATTEMPTS_LEFT = "Invalid confirmation code. No attempts left.";

  @Mock private ConfirmationRepository confirmationRepository;

  @Mock private ConfirmationValidationProperties confirmationValidationProperties;

  @InjectMocks private CodeValidationStep codeValidationStep;

  @Test
  void validate_Success() {
    ConfirmationEntity confirmationEntity = MockData.getValidConfirmationEntity();

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    boolean result = codeValidationStep.validate(validationData);

    assertTrue(result);
  }

  @Test
  void validate_InvalidCode_WithRemainingAttempts() {
    ConfirmationEntity confirmationEntity = MockData.getValidConfirmationEntity();

    ValidationData validationData = new ValidationData(confirmationEntity, 654321);

    when(confirmationValidationProperties.getLastAttemptThreshold()).thenReturn(2);
    when(confirmationValidationProperties.getMaxConfirmAttempts()).thenReturn(3);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> codeValidationStep.validate(validationData));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    assertEquals(TWO_ATTEMPTS_LEFT, exception.getMessage());

    verify(confirmationRepository).save(confirmationEntity);
    assertEquals(1, confirmationEntity.getInvalidAttempts());
  }

  @Test
  void validate_InvalidCode_BlockedAfterAllAttempts() {
    ConfirmationEntity confirmationEntity = MockData.getConfirmationEntityWithLastAttempt();

    ValidationData validationData = new ValidationData(confirmationEntity, 654321);

    when(confirmationValidationProperties.getLastAttemptThreshold()).thenReturn(2);
    when(confirmationValidationProperties.getBlockTimeAfterAllAttempts()).thenReturn(10);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> codeValidationStep.validate(validationData));

    assertEquals(HttpStatus.LOCKED, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains(NO_ATTEMPTS_LEFT));

    verify(confirmationRepository).save(confirmationEntity);
    assertEquals(ConfirmationStatus.BLOCKED, confirmationEntity.getStatus());
    assertNotNull(confirmationEntity.getBlockedAt());
    assertNotNull(confirmationEntity.getBlockedUntil());
  }
}
