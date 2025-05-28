package com.andersenlab.etalon.infoservice.service.chain.validation;

import com.andersenlab.etalon.infoservice.config.properties.ConfirmationValidationProperties;
import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.CodeResendTimeCheckStep;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.StatusCheckStep;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ResendConfirmationValidationChain {
  private final List<ConfirmationCodeValidationStep> steps;

  public ResendConfirmationValidationChain(
      ConfirmationRepository confirmationRepository,
      ConfirmationValidationProperties confirmationValidationProperties) {
    this.steps =
        List.of(
            new CodeResendTimeCheckStep(confirmationValidationProperties),
            new StatusCheckStep(confirmationRepository));
  }

  public boolean validate(ValidationData validationData) {
    boolean result = false;
    for (ConfirmationCodeValidationStep step : steps) {
      result = step.validate(validationData);
    }
    return result;
  }
}
