package com.andersenlab.etalon.depositservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneFieldNotEmptyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneFieldNotEmpty {
  String message() default "At least one field must be provided";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] fields();
}
