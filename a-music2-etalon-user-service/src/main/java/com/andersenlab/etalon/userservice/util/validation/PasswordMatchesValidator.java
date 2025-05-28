package com.andersenlab.etalon.userservice.util.validation;

import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PasswordMatchesValidator
    implements ConstraintValidator<PasswordMatches, CompleteRegistrationRequestDto> {

  @Override
  public void initialize(PasswordMatches constraintAnnotation) {
    // method is empty
  }

  @Override
  public boolean isValid(CompleteRegistrationRequestDto dto, ConstraintValidatorContext context) {
    if (Objects.isNull(dto.password()) || Objects.isNull(dto.repeatedPassword())) {
      return false;
    }
    return dto.password().equals(dto.repeatedPassword());
  }
}
