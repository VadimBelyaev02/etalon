package com.andersenlab.etalon.transactionservice.exception;

public class Temp {
  public static final String TRANSACTION_PRODUCT_NOT_FOUND_BY_ID =
      "Cannot find TRANSACTION product with id %s";
  public static final String TRANSACTION_NOT_FOUND_BY_ACCOUNT_ID =
      "Cannot find ACCOUNT with account id %s";
  public static final String VIEW_TRANSACTIONS_REJECTED_DUE_TO_SECURITY =
      "View transactions for account has been rejected because given account number doesn't belong to this user";
  public static final String NOT_FOUND_ACCOUNT_BY_ID = "Cannot find account entity with id %s";
  public static final String NOT_FOUND_ACCOUNT_BY_NUMBER =
      "Cannot find account entity with number %s";
  public static final String TRANSACTION_NOT_FOUND_BY_TRANSACTION_ID =
      "Cannot find transaction with id %s";
  public static final String NO_EVENT_FOUND_FOR_TRANSACTION =
      "Can not find %s event for transaction with id %s";
  public static final String DETAILED_TRANSACTION_VIEW_REJECTED_DUE_TO_SECURITY =
      "Detailed transaction view has been rejected because given accounts in this transaction doesn't belong to this user";
  public static final String OPERATION_REJECTED_DUE_TO_SECURITY =
      "Operation has been rejected because given account number doesn't belong to this user";
  public static final String NO_PAYMENT_TYPE_FOUND = "Can not find payment type with id %s";
  public static final String OPERATION_REJECTED_BECAUSE_AMOUNT_HAS_MORE_THAN_2_DIGITS_AFTER_DOT =
      "The amount can not have more than 2 digits after the dot";
  public static final String OPERATION_REJECTED_DUE_TO_INCORRECT_PAYMENT_AMOUNT =
      "Minimum amount is 1 ZL";
  public static final String OPERATION_REJECTED_DUE_TO_INCORRECT_TRANSACTION_AMOUNT =
      "Minimum amount is 0.01 ZL";
  public static final String OPERATION_REJECTED_DUE_TO_EMPTY_AMOUNT_FIELD =
      "The operation amount can not be blank";
  public static final String ACCOUNT_IS_BLOCKED =
      "Operation has been rejected because account is blocked";
  public static final String SAME_ACCOUNT_NUMBER =
      "The operation has been rejected because the transfer was made to the same account";
  public static final String CARD_IS_BLOCKED =
      "Operation has been rejected because card is blocked";
  public static final String CARD_IS_EXPIRED =
      "Operation has been rejected because card is expired";
  public static final String NO_CARD_FOUND_BY_ACCOUNT_NUMBER = "No card found by account number %s";
  public static final String TRANSFER_IS_CREATED = "Transfer with id %s has been just created";
  public static final String TEMPLATE_NAME_ALREADY_BEEN_SAVED =
      "Template with that name already exists. Please, use another one";
  public static final String TEMPLATE_NOT_FOUND_BY_ID = "Cannot find TEMPLATE with id %s";
  public static final String PAYMENT_NOT_FOUND_BY_ID = "Cannot find payment with id %s";
  public static final String OPERATION_CONFLICT =
      "The operation has already been successfully processed and cannot be confirmed again.";
  public static final String TRANSFER_NOT_FOUND_BY_ID = "Cannot find transfer with id %s";
  public static final String TRANSFER_TYPE_NOT_FOUND_BY_ID = "Cannot find transfer type with id %s";
  public static final String PRODUCT_TYPE_DOES_NOT_EXIST = "This product type does not exist";
  public static final String THIS_TEMPLATE_DOES_NOT_BELONG_TO_THIS_USER =
      "This template does not belong to this user";
  public static final String THIS_TRANSFER_DOES_NOT_BELONG_TO_THIS_USER =
      "This transfer does not belong to this user";
  public static final String NOT_FOUND_CALCULATOR_BY_CURRENCIES =
      "No calculator found for given currencies";
  public static final String NOT_FOUND_CURRENCY = "Cannot find currency";
  public static final String TRANSFER_IS_DECLINED = "Transfer with id %s was declined";
  public static final String TRANSFER_IS_PROCESSING =
      "Transfer with id %s is currently being processed";
  public static final String PROVIDED_ACCOUNT_NUMBER_IS_NOT_VALID =
      "Provided account number is not valid";
  public static final String NOT_FOUND_EXCHANGE_RATES =
      "Cannot find exchange rates for currency %s";
  public static final String NO_ANY_USER_AUTHORIZED_FOUND = "No any user authorized found";
  public static final String NOT_ALLOW_TO_DELETE_TRANSFER_WHICH_IS_APPROVED_AND_PROCESSING =
      "Not allow to delete the transfer with id %s, which is approved and processing";
  public static final String NOT_ENOUGH_FUNDS_ON_ACCOUNT =
      "Not enough funds in the account with number %s";
  public static final String LIMIT_OF_TRANSFER_AMOUNT_EXCEEDED =
      "Limit of possible transfer amount %s operation for this card has exceeded";
  public static final String DAILY_EXPENSES_LIMIT_EXCEEDED =
      "Limit of possible amount operation per day for this card has exceeded";
  public static final String STATUS_CAN_NOT_BE_NULL = "Status can not be null";
  public static final String UNKNOWN_TRANSACTION_STATUS = "Unknown TransactionStatus: %s";
  public static final String DIVIDE_BY_NULL_OR_ZERO = "Cannot divide by null or zero";
  public static final String MATHEMATICAL_OPERATIONS_WITH_NULL =
      "Cannot perform mathematical operations with null";
}
