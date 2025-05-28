package com.andersenlab.etalon.transactionservice.util;

import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;

@UtilityClass
public class Constants {
  public static final String AUTHENTICATED_USER_ID = "authenticated-user-id";
  public static final String APPROVED = "APPROVED";
  public static final String CREATED = "CREATED";
  public static final String OPEN_DEPOSIT = "OPEN_DEPOSIT";
  public static final String TRANSFER = "TRANSFER";
  public static final String ACCOUNT_NUMBER_PATTERN = "[A-Z]{2}\\d{26}";
  public static final String WRONG_ACCOUNT_NUMBER_MESSAGE =
      "IBAN should start with two letters followed by 26 digits, 28 characters in total";
  public static final String BLANK_BENEFICIARY = "Beneficiary account number is required";
  public static final String BLANK_SOURCE = "Source account number is required";
  public static final String BLANK_ACCOUNT_NUMBER = "Account number is required";
  public static final String INVALID_AMOUNT = "Invalid transaction amount";
  public static final String DEFAULT_CURRENCY = "PLN";
  public static final LinkedMultiValueMap<String, String> GET_DEFAULT_CURRENCY_QUERY_PARAMS =
      new LinkedMultiValueMap<>(Map.of("currency", List.of(DEFAULT_CURRENCY)));
  public static final Integer MONTH_GAP = 6;
  public static final String TRACE_ID = "traceId";
  public static final String PAYMENT_PRODUCT_ID = "Payment product id is required";
}
