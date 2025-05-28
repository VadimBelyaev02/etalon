package com.andersenlab.etalon.infoservice.service.chain.validation.step;

import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeValidationStep implements ConfirmationCodeValidationStep {

  private final ConfirmationRepository confirmationRepository;
  private final ConfirmationValidationProperties confirmationValidationProperties;

  @Override
  public boolean validate(ValidationData validationData) {
    ConfirmationEntity confirmationEntity = validationData.confirmationEntity();
    if (!confirmationEntity.getConfirmationCode().equals(validationData.confirmationCode())) {

      if (confirmationEntity.getInvalidAttempts()
          >= confirmationValidationProperties.getLastAttemptThreshold()) {
        confirmationEntity.setBlockedAt(ZonedDateTime.now());
        confirmationEntity.setBlockedUntil(
            ZonedDateTime.now()
                .plusMinutes(confirmationValidationProperties.getBlockTimeAfterAllAttempts()));
        confirmationEntity.setStatus(ConfirmationStatus.BLOCKED);
        confirmationRepository.save(confirmationEntity);
        throw new BusinessException(
            HttpStatus.LOCKED,
            "Invalid confirmation code. No attempts left. Confirmation is blocked for 10 minutes, will be unblock at <%s>"
                .formatted(confirmationEntity.getBlockedUntil()));
      }

      confirmationEntity.incrementInvalidAttempts();
      confirmationRepository.save(confirmationEntity);

      throw new BusinessException(
          HttpStatus.BAD_REQUEST,
          "You have entered invalid code. %d attempts left."
              .formatted(
                  confirmationValidationProperties.getMaxConfirmAttempts()
                      - validationData.confirmationEntity().getInvalidAttempts()));
    }
    log.info("{CodeValidationStep} validation passed");
    return true;
  }
}
