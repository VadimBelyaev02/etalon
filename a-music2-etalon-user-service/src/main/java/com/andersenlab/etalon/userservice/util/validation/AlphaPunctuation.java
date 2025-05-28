package com.andersenlab.etalon.userservice.util.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to validate String fields
 *
 * <p>Field <b>valid</b> when:
 *
 * <p>1. <b>null</b>
 *
 * <p>or
 *
 * <p>1. Starts with <b>uppercase</b> letter
 *
 * <p>2. May contain <b>uppercase/lowercase</b> letters, <b>dashes</b> and <b>spaces</b>
 *
 * <p>3. Can`t ends with <b>dash</b> and <b>space</b>
 *
 * <p>
 *
 * <p>
 *
 * @see AlphaPunctuationValidator
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = AlphaPunctuationValidator.class)
public @interface AlphaPunctuation {
  String message() default "Invalid alpha punctuation value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
