package com.andersenlab.etalon.infoservice.service.chain.validation;

import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.BlockingStatusCheckStep;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.CodeValidationStep;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.ExpiredCodeStep;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationValidationChain {
  private final List<ConfirmationCodeValidationStep> steps;

  public ConfirmationValidationChain(
      ConfirmationRepository confirmationRepository,
      ConfirmationValidationProperties confirmationValidationProperties) {
    this.steps =
        List.of(
            new BlockingStatusCheckStep(),
            new ExpiredCodeStep(confirmationRepository, confirmationValidationProperties),
            new CodeValidationStep(confirmationRepository, confirmationValidationProperties));
  }

  public boolean validate(ValidationData validationData) {
    boolean result = false;
    for (ConfirmationCodeValidationStep step : steps) {
      result = step.validate(validationData);
    }
    return result;
  }
}
