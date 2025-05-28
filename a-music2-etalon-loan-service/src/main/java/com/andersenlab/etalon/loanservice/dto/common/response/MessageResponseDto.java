package com.andersenlab.etalon.loanservice.dto.common.response;

public record MessageResponseDto(String message) {

  public static final String SEND_LOAN_ORDER_TO_REVIEW =
      "Your request to open a loan has been sent to review";
  public static final String LOAN_OPENED = "Loan has been opened successfully";
  public static final String TRANSACTION_IS_SUCCESSFUL = "Transaction with id %s was successful";
  public static final String PAYMENT_SUCCEEDED = "Loan balance has been changed";
  public static final String LOAN_CLOSED_SUCCESSFULLY = "The loan has been fully repaid";
}
