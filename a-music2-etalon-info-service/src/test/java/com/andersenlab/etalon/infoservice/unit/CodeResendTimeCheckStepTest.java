package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.CodeResendTimeCheckStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class CodeResendTimeCheckStepTest {
  private static final String TOO_MANY_REQUESTS_MESSAGE =
      "Too many requests of confirmation code. Try again after";

  @Mock private ConfirmationValidationProperties confirmationValidationProperties;

  @InjectMocks private CodeResendTimeCheckStep codeResendTimeCheckStep;

  @Test
  void validate_Success() {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime lastRequestTime = now.minusMinutes(5);

    ConfirmationEntity confirmationEntity = new ConfirmationEntity();
    confirmationEntity.setUpdateAt(lastRequestTime);
    confirmationEntity.setStatus(ConfirmationStatus.CREATED);

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    when(confirmationValidationProperties.getNextCodeResendTime()).thenReturn(1);

    boolean result = codeResendTimeCheckStep.validate(validationData);

    assertTrue(result);
  }

  @Test
  void validate_TooManyRequests() {
    ConfirmationEntity confirmationEntity = new ConfirmationEntity();
    confirmationEntity.setUpdateAt(ZonedDateTime.now());
    confirmationEntity.setStatus(ConfirmationStatus.CREATED);

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    when(confirmationValidationProperties.getNextCodeResendTime()).thenReturn(1);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> codeResendTimeCheckStep.validate(validationData));

    assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
    assertTrue(exception.getMessage().contains(TOO_MANY_REQUESTS_MESSAGE));
  }
}
