package com.andersenlab.etalon.accountservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessExceptionQ extends RuntimeException {

  public static final String NOT_FOUND_ACCOUNT_BY_ID = "Cannot find account entity with id %s";
  public static final String NOT_FOUND_ACCOUNT_BY_ACCOUNT_NUMBER =
      "Cannot find account entity with account number %s with provided userId";
  public static final String NOT_FOUND_ACCOUNT_BY_IBAN_AND_USER_ID =
      "Cannot find account entity with account number '%s' and user ID '%s'";
  public static final String NOT_FOUND_ACCOUNTS_BY_USER_ID =
      "Cannot find accounts with user ID '%s'";
  public static final String NOT_ENOUGH_FUNDS_ON_ACCOUNT =
      "Not enough funds in the account with number %s";
  public static final String NOT_ENOUGH_FUNDS_ON_ACCOUNT_WITH_ID =
      "Not enough funds in the account with id %s";
  public static final String NO_ACTIVE_ACCOUNTS = "The user has no active accounts";
  public static final String NOT_ENOUGH_DATA =
      "Insufficient data provided: last name or first name is missing";
  public static final String ACCOUNT_DELETION_RESTRICTION_MESSAGE =
      "An account with a positive balance cannot be deleted";

  private final HttpStatus httpStatus;

  public BusinessExceptionQ(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
