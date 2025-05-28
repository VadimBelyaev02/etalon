package com.andersenlab.etalon.infoservice.service.chain.validation.step;

import static com.andersenlab.etalon.infoservice.exception.BusinessException.CONFIRMATION_CONFLICT;

import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.exception.ConfirmationBlockedException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
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
public class StatusCheckStep implements ConfirmationCodeValidationStep {
  private final ConfirmationRepository confirmationRepository;

  @Override
  public boolean validate(ValidationData validationData) {
    ConfirmationEntity confirmationEntity = validationData.confirmationEntity();
    switch (confirmationEntity.getStatus()) {
      case BLOCKED -> {
        if (Objects.nonNull(confirmationEntity.getBlockedUntil())
            && confirmationEntity.getBlockedUntil().isBefore(ZonedDateTime.now())) {
          confirmationEntity.setStatus(ConfirmationStatus.CREATED);
          confirmationRepository.save(confirmationEntity);
          log.info("{StatusCheckStep} blocking time over, confirmation status change to CREATED");
        } else {
          throw new ConfirmationBlockedException(HttpStatus.LOCKED, confirmationEntity);
        }
      }
      case CONFIRMED -> throw new BusinessException(HttpStatus.CONFLICT, CONFIRMATION_CONFLICT);
      default -> {
        log.info(
            "{StatusCheckStep} status check for status-%s skipped"
                .formatted(confirmationEntity.getStatus()));
        return true;
      }
    }
    log.info("{StatusCheckStep} validation passed");
    return true;
  }
}
