package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidTransactionCreateRequestDto;
import static com.andersenlab.etalon.transactionservice.MockData.getValidTransactionCreateRequestDtoWithNoCard;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransactionController;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.TransactionCreateRequestDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class CreateTransactionEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "1";

  @Test
  @WithUserId
  void whenCreateNewTransactionWhenReceiverHasNoCard_shouldSucceed() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto =
        getValidTransactionCreateRequestDtoWithNoCard();

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(toJson(transactionCreateRequestDto)))
        .andDo(print())
        .andExpect(status().isCreated());
  }

  @Test
  @WithUserId
  void whenCreateNewTransactionWithZeroAmount_shouldFail() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto =
        getValidTransactionCreateRequestDto().toBuilder().amount(BigDecimal.valueOf(0)).build();

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionCreateRequestDto)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath(
                "$.message",
                is(BusinessException.OPERATION_REJECTED_DUE_TO_INCORRECT_TRANSACTION_AMOUNT)));
  }

  @Test
  @WithUserId
  void whenCreateNewTransactionWithNegativeAmount_shouldFail() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto =
        getValidTransactionCreateRequestDto().toBuilder().amount(BigDecimal.valueOf(-1)).build();

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionCreateRequestDto)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath(
                "$.message",
                is(BusinessException.OPERATION_REJECTED_DUE_TO_INCORRECT_TRANSACTION_AMOUNT)));
  }

  @Test
  @WithUserId
  void whenCreateNewTransactionWithWrongAmountPrecision_shouldFail() throws Exception {
    TransactionCreateRequestDto transactionCreateRequestDto =
        getValidTransactionCreateRequestDto().toBuilder().amount(BigDecimal.valueOf(3.265)).build();

    mockMvc
        .perform(
            post(TransactionController.TRANSACTIONS_V1_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(transactionCreateRequestDto)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath(
                "$.message",
                is(
                    BusinessException
                        .OPERATION_REJECTED_BECAUSE_AMOUNT_HAS_MORE_THAN_2_DIGITS_AFTER_DOT)));
  }
}
