package com.andersenlab.etalon.depositservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
  public static final String DEPOSIT_PRODUCT_NOT_FOUND = "Cannot find deposit product with id %s";
  public static final String OPERATION_REJECTED_INVALID_ACCOUNT_NUMBER =
      "Operation for user has been rejected because given account number is invalid";
  public static final String TRANSACTION_REJECTED_ACCOUNT_BLOCKED =
      "Transaction for user has been rejected because given account is blocked";
  public static final String DEPOSIT_REPLENISH_REJECTED_MINIMAL_AMOUNT =
      "The minimum amount is 1zl";
  public static final String DEPOSIT_REPLENISH_REJECTED_MAXIMUM_AMOUNT =
      "The entered amount exceeds the maximum amount under the terms of your deposit";
  public static final String DEPOSIT_REPLENISH_REJECTED_INSUFFICIENT_FUNDS =
      "There is insufficient funds in your account to process the transaction";
  public static final String DEPOSIT_IS_EXPIRED = "Deposit is expired";
  public static final String DEPOSIT_REPLENISH_REJECTED_NOT_ENOUGH_AMOUNT =
      "There is not enough amount to open new deposit";
  public static final String DEPOSIT_ORDER_NOT_FOUND_BY_ID = "Cannot find deposit order with id %s";
  public static final String DEPOSIT_ORDER_NOT_FOUND_BY_TRANSACTION_ID =
      "Cannot find deposit order with transaction id %s";
  public static final String DEPOSIT_NOT_FOUND_BY_ID = "Cannot find deposit with id %s";
  public static final String DEPOSIT_FULL_WITHDRAWAL_INSUFFICIENT_FUNDS =
      "The entered amount is more than the current amount on the deposit. Transaction is not possible";
  public static final String DEPOSIT_PARTIAL_WITHDRAWAL_INSUFFICIENT_FUNDS =
      "The entered amount is more than available for partial withdrawal";
  public static final String DEPOSIT_WITHDRAWAL_MIN_AMOUNT =
      "The minimum deposit amount to withdraw is 1 zl";
  public static final String DEPOSIT_WITHDRAWAL_INVALID_AMOUNT_SCALE =
      "The withdraw amount can not have more than 2 digits after the dot";
  public static final String WITHDRAWAL_ACCOUNT_SAME_AS_REPLENISH =
      "The withdraw account number and replenish account number can not be the same";
  public static final String TRANSACTION_REJECTED_EARLY_WITHDRAWAL_NOT_SUPPORTED =
      "Transaction for user has been rejected because given deposit does not support early withdrawal";
  public static final String DEPOSIT_INVALID_AMOUNT_SCALE =
      "The replenish amount can not have more than 2 digits after the dot";
  public static final String TRANSACTION_REJECTED_INSUFFICIENT_BALANCE =
      "Transaction for user has been rejected due to insufficient balance";
  public static final String INVALID_AMOUNT_SCALE =
      "The amount can not have more than 2 digits after the dot";
  public static final String AMOUNT_MUST_BE_POSITIVE = "The amount must be positive";
  public static final String VALUE_LESS_MINIMUM_REQUIRED =
      "Value is less than the minimum required";
  public static final String VALUE_MORE_MAXIMUM_REQUIRED =
      "Value is more than the maximum required";
  public static final String NO_BALANCE_FOUND_FOR_DEPOSIT =
      "No balance found for deposit with id: %s";
  public static final String DEPOSIT_BALANCE_NOT_FOUND =
      "There is no balance for deposit with id %s";
  public static final String AMOUNT_FROM_REQUEST_NULL =
      "Deposit's withdraw amount from request shouldn't be null.";
  public static final String INVALID_PAGE_NUMBER = "Page number should'nt be negative";
  public static final String INVALID_PAGE_SIZE = "Page size should be greater than 0";
  public static final String NO_SUCH_FIELD = "Requested field does not exist";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String DEPOSIT_UPDATE_REJECTED_SAME_ACCOUNTS =
      "Deposit update rejected due to the same account number provided";

  private final HttpStatus httpStatus;

  public BusinessException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
