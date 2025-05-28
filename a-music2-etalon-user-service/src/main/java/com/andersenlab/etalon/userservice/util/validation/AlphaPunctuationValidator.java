package com.andersenlab.etalon.userservice.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import java.util.regex.Pattern;

public class AlphaPunctuationValidator implements ConstraintValidator<AlphaPunctuation, String> {

  private static final String ALPHA_PUNCTUATION = "^[A-Z][a-zA-Z -]*[a-zA-Z]$";
  private static final Pattern UPPER_CASE =
      Pattern.compile(ALPHA_PUNCTUATION, Pattern.UNICODE_CASE);

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return Objects.isNull(value) || UPPER_CASE.matcher(value).matches();
  }
}
