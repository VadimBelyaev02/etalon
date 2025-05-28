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
public class ExpiredCodeStep implements ConfirmationCodeValidationStep {

  private final ConfirmationRepository confirmationRepository;
  private final ConfirmationValidationProperties confirmationValidationProperties;

  @Override
  public boolean validate(ValidationData validationData) {
    ConfirmationEntity confirmationEntity = validationData.confirmationEntity();
    ZonedDateTime lastUpdate = confirmationEntity.getUpdateAt();
    ZonedDateTime codeExpiresAt =
        lastUpdate.plusMinutes(confirmationValidationProperties.getConfirmCodeExpirationTime());
    ZonedDateTime now = ZonedDateTime.now();

    log.info("{ExpiredCodeStep} Code last update time: {}", lastUpdate);
    log.info("{ExpiredCodeStep} Code expiration time: {}", codeExpiresAt);

    if (confirmationEntity.getStatus() == ConfirmationStatus.EXPIRED) {
      throw new BusinessException(
          HttpStatus.GONE, "This code has expired. Please, request a new code");
    }

    if (now.isAfter(codeExpiresAt)) {
      confirmationEntity.setStatus(ConfirmationStatus.EXPIRED);
      confirmationRepository.save(confirmationEntity);

      throw new BusinessException(
          HttpStatus.GONE, "This code has expired. Please, request a new code");
    }

    log.info("{ExpiredCodeStep} validation passed");
    return true;
  }
}
