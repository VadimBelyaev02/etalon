package com.andersenlab.etalon.infoservice.service.chain.validation.step;

import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.ConfirmationBlockedException;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockingStatusCheckStep implements ConfirmationCodeValidationStep {

  @Override
  public boolean validate(ValidationData validationData) {
    ConfirmationEntity confirmationEntity = validationData.confirmationEntity();
    if (Objects.nonNull(confirmationEntity.getBlockedUntil())
        && ConfirmationStatus.BLOCKED.equals(confirmationEntity.getStatus())
        && validationData.confirmationEntity().getBlockedUntil().isAfter(ZonedDateTime.now())) {
      throw new ConfirmationBlockedException(HttpStatus.LOCKED, confirmationEntity);
    }
    log.info("{BlockingStatusCheckStep} validation passed");
    return true;
  }
}
