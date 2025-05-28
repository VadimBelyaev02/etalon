package com.andersenlab.etalon.infoservice.service;

import com.andersenlab.etalon.infoservice.entity.ValidationData;

public interface ConfirmationCodeValidationStep {
  boolean validate(ValidationData validationData);
}
