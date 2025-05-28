package com.andersenlab.etalon.infoservice.service.chain.validation;

import com.andersenlab.etalon.infoservice.entity.ValidationData;
import com.andersenlab.etalon.infoservice.repository.ConfirmationRepository;
import com.andersenlab.etalon.infoservice.service.ConfirmationCodeValidationStep;
import com.andersenlab.etalon.infoservice.service.chain.validation.step.TargetIdStatusCheckStep;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CreateConfirmationValidationChain {
  private final List<ConfirmationCodeValidationStep> steps;

  public CreateConfirmationValidationChain(ConfirmationRepository confirmationRepository) {
    this.steps = List.of(new TargetIdStatusCheckStep(confirmationRepository));
  }

  public boolean validate(ValidationData validationData) {
    boolean result = false;
    for (ConfirmationCodeValidationStep step : steps) {
      result = step.validate(validationData);
    }
    return result;
  }
}
