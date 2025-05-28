package com.andersenlab.etalon.userservice.util.validation;

import static com.andersenlab.etalon.userservice.util.Constants.INVALID_CHARACTER_ERROR;
import static com.andersenlab.etalon.userservice.util.Constants.STARTS_OR_ENDS_WITH_SPACE_OR_HYPHEN_ERROR;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.regex.Pattern;

public class ValidWorkFieldValidator implements ConstraintValidator<ValidWorkField, String> {
  private String fieldName;
  private static final String VALID_REGEX = "^(?![-\\s])[A-Za-z0-9\"'«»\\-.,() ]{2,}(?<![-\\s])$";

  private static final Pattern VALID_PATTERN = Pattern.compile(VALID_REGEX);

  @Override
  public boolean isValid(String input, ConstraintValidatorContext context) {
    if (Objects.isNull(input)) {
      return true;
    }
    if (isStartingOrEndingWithInvalidCharacters(input)) {
      addConstraintViolation(
          context, String.format(STARTS_OR_ENDS_WITH_SPACE_OR_HYPHEN_ERROR, fieldName));
      return false;
    }
    if (!isValidPattern(input)) {
      addConstraintViolation(context, String.format(INVALID_CHARACTER_ERROR, fieldName));
      return false;
    }
    return true;
  }

  private boolean isStartingOrEndingWithInvalidCharacters(String input) {
    return input.startsWith(" ")
        || input.startsWith("-")
        || input.endsWith(" ")
        || input.endsWith("-");
  }

  private boolean isValidPattern(String input) {
    return VALID_PATTERN.matcher(input).matches();
  }

  private void addConstraintViolation(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }

  @Override
  public void initialize(ValidWorkField constraintAnnotation) {
    this.fieldName = constraintAnnotation.fieldName();
  }
}
