package com.andersenlab.etalon.userservice.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidWorkFieldValidator.class)
public @interface ValidWorkField {
  String fieldName();

  String message() default "Invalid input";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
