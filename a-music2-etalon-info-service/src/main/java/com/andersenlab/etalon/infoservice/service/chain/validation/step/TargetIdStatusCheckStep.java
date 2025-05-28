package com.andersenlab.etalon.infoservice.service.chain.validation.step;

import com.andersenlab.etalon.infoservice.entity.ConfirmationEntity;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.exception.ConfirmationBlockedException;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TargetIdStatusCheckStep implements ConfirmationCodeValidationStep {

  private final ConfirmationRepository confirmationRepository;

  @Override
  public boolean validate(ValidationData validationData) {

    Optional<ConfirmationEntity> latestConfirmationEntityOptional =
        confirmationRepository
            .findByTargetIdAndOperation(
                validationData.confirmationEntity().getTargetId(),
                validationData.confirmationEntity().getOperation())
            .stream()
            .max(Comparator.comparing(ConfirmationEntity::getUpdateAt));
    if (latestConfirmationEntityOptional.isPresent()) {
      ConfirmationEntity confirmationEntity = latestConfirmationEntityOptional.get();
      switch (confirmationEntity.getStatus()) {
        case BLOCKED -> throw new ConfirmationBlockedException(
            HttpStatus.LOCKED, confirmationEntity);
        case REJECTED -> throw new BusinessException(
            HttpStatus.CONFLICT,
            "Confirmation with id-%s is in rejected status".formatted(confirmationEntity.getId()));
        case CREATED -> throw new BusinessException(
            HttpStatus.CONFLICT,
            "<CREATED> Confirmation is already created, check email for confirmation code sent");
        default -> log.info(
            "{TargetIdStatusCheckStep.validate} Validation passed for confirmation with id-%s, targetId-%s, status-%s"
                .formatted(
                    confirmationEntity.getId(),
                    confirmationEntity.getTargetId(),
                    confirmationEntity.getStatus()));
      }
    }
    return true;
  }
}
