package com.andersenlab.etalon.userservice.util.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
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
 * <p>1. Starts with <b>uppercase/lowercase</b> letters or <b>digits</b>
 *
 * <p>2. May contain <b>uppercase/lowercase</b> letters, <b>digits</b>, <b>dashes</b> and
 * <b>spaces</b>
 *
 * <p>3. Can`t ends with <b>dash</b> and <b>space</b>
 *
 * <p>
 *
 * <p>
 *
 * @see AlphaPunctuationNumericValidator
 */
@Constraint(validatedBy = AlphaPunctuationNumericValidator.class)
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface AlphaPunctuationNumeric {
  String message() default "Invalid alpha punctuation numeric value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
