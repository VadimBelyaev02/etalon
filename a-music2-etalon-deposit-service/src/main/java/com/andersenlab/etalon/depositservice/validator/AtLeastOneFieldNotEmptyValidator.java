package com.andersenlab.etalon.depositservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import org.apache.commons.lang3.ObjectUtils;

public class AtLeastOneFieldNotEmptyValidator
    implements ConstraintValidator<AtLeastOneFieldNotEmpty, Record> {

  private String[] fields;

  @Override
  public void initialize(AtLeastOneFieldNotEmpty constraintAnnotation) {
    this.fields = constraintAnnotation.fields();
  }

  @Override
  public boolean isValid(Record rec, ConstraintValidatorContext context) {
    boolean isValid =
        Arrays.stream(rec.getClass().getRecordComponents())
            .filter(rc -> Arrays.asList(fields).contains(rc.getName()))
            .map(rc -> getFieldValue(rec, rc))
            .anyMatch(value -> !ObjectUtils.isEmpty(value));

    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "At least one of the fields must be provided: " + String.join(", ", fields))
          .addConstraintViolation();
    }

    return isValid;
  }

  private Object getFieldValue(Record rec, RecordComponent rc) {
    try {
      return rc.getAccessor().invoke(rec);
    } catch (Exception e) {
      throw new RuntimeException("Failed to access field value", e);
    }
  }
}
