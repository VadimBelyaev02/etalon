package com.andersenlab.etalon.cardservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

  public static final String NOT_FOUND_CARD_BY_ID = "Cannot find card entity with id %s";
  public static final String NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST =
      "User card change status request could not be completed";
  public static final String CARD_CANNOT_UNBLOCK =
      "The card cannot be unlocked. You should contact the bank to unblock the card";
  public static final String CARD_CANNOT_UNBLOCK_NEVER =
      "Sorry, the card cannot be unlocked. You should order a new card";
  public static final String CARD_PRODUCT_NOT_FOUND = "Card product was not found with id %s";
  public static final String CURRENCIES_DOESNT_MATCH = "Card product has different currencies";
  public static final String NOT_FOUND_CARD_BY_NUMBER = "Cannot find card entity with number %s";
  public static final String NOT_FOUND_CARD_BY_ACCOUNT_NUMBER =
      "Some of the provided account numbers are not linked to a user's card";
  public static final String CARD_CURRENCY_WAS_NOT_FOUND_IN_REQUEST =
      "Card currency was not found in request";
  public static final String CURRENCY_LIMITS_NOT_FOUND =
      "Currency limits not found for requested currency";
  public static final String CARD_ACCOUNT_LINKED_TO_DEPOSIT =
      "This card's account is linked to the deposit";
  public static final String AVAILABLE_CURRENCIES_NOT_FOUND = "Available currencies not found";
  public static final String INVALID_WITHDRAW_LIMIT = "Invalid amount of withdraw limit";
  public static final String INVALID_TRANSFER_LIMIT = "Invalid amount of transfer limit";
  public static final String INVALID_DAILY_EXPENSE_LIMIT = "Invalid amount of daily expense limit";
  public static final String ACCOUNT_IS_ALREADY_LINKED_TO_ANOTHER_ACTIVE_CARD =
      "Current account is already linked to another active card";
  public static final String ACCOUNT_IS_BLOCKED = "Account is blocked";
  public static final String ACCOUNT_LINKED_TO_ANOTHER_USER =
      "Current account is linked to another user";
  public static final String CARD_IS_BLOCKED =
      "The card is blocked and the card limits have not been updated";
  public static final String CARD_IS_ALREADY_BLOCKED = "The card is already blocked";

  private final HttpStatus httpStatus;

  public BusinessException(final HttpStatus httpStatus, final String message) {
    super(message);
    this.httpStatus = httpStatus;
  }
}
