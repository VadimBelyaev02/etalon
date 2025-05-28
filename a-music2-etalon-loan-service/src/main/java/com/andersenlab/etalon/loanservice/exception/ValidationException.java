package com.andersenlab.etalon.loanservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException {
  public static final String INCORRECT_AMOUNT =
      "The amount goes outside of the selected loan product";
  public static final String INCORRECT_DURATION = "Incorrect loan duration";
  public static final String SALARY_LESS_THAN_EXPENSES =
      "Your salary is less than your monthly expenses";

  public static final String GUARANTOR_ALREADY_EXIST =
      "The guarantor with entered data wasn't found. Please check the information";

  public static final String INVALID_GUARANTOR_COUNT =
      "The count of guarantors specified does not match the count of required guarantors";

  private final HttpStatus httpStatus;

  public ValidationException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
