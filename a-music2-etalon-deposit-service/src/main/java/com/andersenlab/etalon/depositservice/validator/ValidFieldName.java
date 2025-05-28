package com.andersenlab.etalon.depositservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidFieldNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFieldName {
  String message() default "Invalid field name";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<?> targetClass();
}
