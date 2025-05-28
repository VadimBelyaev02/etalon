package com.andersenlab.etalon.depositservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.ObjectUtils;

public class ValidFieldNameValidator implements ConstraintValidator<ValidFieldName, String> {

  private Class<?> targetClass;

  @Override
  public void initialize(ValidFieldName constraintAnnotation) {
    this.targetClass = constraintAnnotation.targetClass();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (ObjectUtils.isEmpty(value)) {
      return true;
    }
    List<String> validFields =
        Arrays.stream(targetClass.getDeclaredFields()).map(Field::getName).toList();
    return validFields.contains(value);
  }
}
