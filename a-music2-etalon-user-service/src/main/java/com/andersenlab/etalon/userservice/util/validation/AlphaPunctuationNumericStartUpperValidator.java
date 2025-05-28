package com.andersenlab.etalon.userservice.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.regex.Pattern;

public class AlphaPunctuationNumericStartUpperValidator
    implements ConstraintValidator<AlphaPunctuationNumericStartUpper, String> {

  private static final String ALPHA_PUNCTUATION_NUMERIC_STARTS_UPPERCASE =
      "(^$)|(^[A-Z][\\w \\-\\\\()]*[A-Za-z0-9\\\\)]$)";
  private static final Pattern UPPER_CASE =
      Pattern.compile(ALPHA_PUNCTUATION_NUMERIC_STARTS_UPPERCASE, Pattern.UNICODE_CASE);

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return Objects.isNull(value) || UPPER_CASE.matcher(value).matches();
  }
}
