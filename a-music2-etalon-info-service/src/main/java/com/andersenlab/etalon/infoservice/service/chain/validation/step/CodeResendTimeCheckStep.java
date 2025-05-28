package com.andersenlab.etalon.infoservice.service.chain.validation.step;

import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.exception.BusinessException;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import com.andersenlab.etalon.infoservice.util.enums.ConfirmationStatus;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeResendTimeCheckStep implements ConfirmationCodeValidationStep {

  private final ConfirmationValidationProperties confirmationValidationProperties;

  @Override
  public boolean validate(ValidationData validationData) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime lastRequestTime = validationData.confirmationEntity().getUpdateAt();
    ZonedDateTime nextAllowedRequestTime =
        lastRequestTime.plusMinutes(confirmationValidationProperties.getNextCodeResendTime());
    if (ConfirmationStatus.CREATED.equals(validationData.confirmationEntity().getStatus())
        && now.isBefore(nextAllowedRequestTime)) {
      throw new BusinessException(
          HttpStatus.FORBIDDEN,
          "Too many requests of confirmation code. Try again after <%s>"
              .formatted(nextAllowedRequestTime));
    }
    log.info("{CodeResendTimeCheckStep} validation passed");
    return true;
  }
}
