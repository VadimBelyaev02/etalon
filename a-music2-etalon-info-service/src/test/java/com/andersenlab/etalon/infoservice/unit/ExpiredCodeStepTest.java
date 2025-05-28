package com.andersenlab.etalon.infoservice.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.andersenlab.etalon.infoservice.MockData;
import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.ExpiredCodeStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class ExpiredCodeStepTest {
  private static final String CODE_EXPIRED_MESSAGE =
      "This code has expired. Please, request a new code";
  private static final int EXPIRATION_TIME_MINUTES = 5;

  @Mock private ConfirmationRepository confirmationRepository;

  @Mock private ConfirmationValidationProperties confirmationValidationProperties;

  @InjectMocks private ExpiredCodeStep expiredCodeStep;

  @Test
  void validate_Success_WhenCodeNotExpired() {
    when(confirmationValidationProperties.getConfirmCodeExpirationTime())
        .thenReturn(EXPIRATION_TIME_MINUTES);

    ConfirmationEntity confirmationEntity = MockData.getValidConfirmationEntity();

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    boolean result = expiredCodeStep.validate(validationData);

    assertTrue(result);
    verifyNoInteractions(confirmationRepository);
  }

  @Test
  void validate_ThrowsException_WhenCodeExpired() {
    when(confirmationValidationProperties.getConfirmCodeExpirationTime())
        .thenReturn(EXPIRATION_TIME_MINUTES);

    ConfirmationEntity confirmationEntity = MockData.getExpiredConfirmationEntity();

    ValidationData validationData = new ValidationData(confirmationEntity, 123456);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> expiredCodeStep.validate(validationData));

    assertEquals(HttpStatus.GONE, exception.getHttpStatus());
    assertEquals(CODE_EXPIRED_MESSAGE, exception.getMessage());

    assertEquals(ConfirmationStatus.EXPIRED, confirmationEntity.getStatus());
    verify(confirmationRepository).save(confirmationEntity);
  }
}
