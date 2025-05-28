package com.andersenlab.etalon.userservice.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class AlphaPunctuationNumericValidator
    implements ConstraintValidator<AlphaPunctuationNumeric, String> {

  private static final String ALPHA_PUNCTUATION_NUMERIC = "^\\w[\\w \\-\\\\()]*[\\w\\\\)]$";
  private static final Pattern ANY_CASE =
      Pattern.compile(ALPHA_PUNCTUATION_NUMERIC, Pattern.UNICODE_CASE);

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return StringUtils.isEmpty(value) || ANY_CASE.matcher(value).matches();
  }
}
