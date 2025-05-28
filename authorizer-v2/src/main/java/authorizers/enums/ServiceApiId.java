package authorizers.enums;

import lombok.Getter;

@Getter
public enum ServiceApiId {
  INFO("INFO_API_ID"),
  LOAN("LOAN_API_ID"),
  CARD("CARD_API_ID"),
  DEPOSIT("DEPOSIT_API_ID"),
  USER("USER_API_ID"),
  ACCOUNT("ACCOUNT_API_ID"),
  TRANSACTION("TRANSACTION_API_ID");
  public final String serviceIdName;

  ServiceApiId(String serviceIdName) {
    this.serviceIdName = serviceIdName;
  }
}
