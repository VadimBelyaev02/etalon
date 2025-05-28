package com.andersenlab.etalon.loanservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  public static final String LOAN_ORDER_NOT_FOUND_BY_ID = "Cannot find loan order with id %s";
  public static final String LOAN_NOT_FOUND_BY_ID = "Cannot find loan with id %s";
  public static final String LOAN_PRODUCT_NOT_FOUND_BY_ID = "Cannot find loan product with id %s";
  public static final String LOAN_ORDER_WITH_ID_REJECTED =
      "Loan order with ID: %s has been rejected";
  public static final String LOAN_ORDER_WITH_ID_IN_REVIEW =
      "Loan order with ID: %s is still in review";
  public static final String LOAN_ORDER_WITH_ID_CLOSED = "Loan order with ID: %s was closed";
  public static final String LOAN_PAYMENT_REJECTED_DUE_TO_EXCESSIVE_AMOUNT =
      "Payment for loan has been rejected because you tried to replenish excessive amount of funds";
  public static final String LOAN_PAYMENT_REJECTED_DUE_TO_INSUFFICIENT_AMOUNT =
      "Payment for loan has been rejected because you tried to replenish insufficient amount of funds";
  public static final String TRANSACTION_REJECTED_DUE_TO_INVALID_ACCOUNT_NUMBER =
      "Transaction for user has been rejected because given account number doesn't belong to this user";
  public static final String TRANSACTION_REJECTED_DUE_TO_ACCOUNT_BLOCKING =
      "Transaction for user has been rejected because given account is blocked";
  public static final String INSUFFICIENT_FUNDS_FOR_LOAN_PAYMENT =
      "Insufficient funds to repay the loan";
  public static final String PAYMENT_AMOUNT_HAS_CHANGED_TRY_AGAIN =
      "Payment amount has changed. Please try again";
  public static final String UNSUPPORTED_LOAN_STATUS = "Unsupported loan status: %s";
  public static final String INVALID_REQUEST_DATA = "Invalid request data";
  private final HttpStatus httpStatus;

  public BusinessException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
