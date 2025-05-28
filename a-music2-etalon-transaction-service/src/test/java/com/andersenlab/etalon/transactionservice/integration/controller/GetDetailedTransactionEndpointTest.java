package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransactionController;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class GetDetailedTransactionEndpointTest extends AbstractIntegrationTest {

  public static final String SENDER_ID = "3";
  public static final String USER_ID = "1";
  public static final String TRANSACTION_ID = "5";
  public static final String TRANSACTION_ID_CASE_7 = "7";

  @Test
  @WithUserId("3")
  void whenGetDetailedTransaction_thenSuccess() throws Exception {
    ZonedDateTime zonedDateTime = ZonedDateTime.of(2023, 9, 30, 11, 0, 0, 0, ZoneId.of("CET"));
    String date = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(zonedDateTime);

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL + "/" + TRANSACTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(5)))
        .andExpect(jsonPath("$.transactionAmount", is(10)))
        .andExpect(jsonPath("$.currency", is("PLN")))
        .andExpect(jsonPath("$.status", is("APPROVED")))
        .andExpect(jsonPath("$.transactionName", is("transactionName")))
        .andExpect(jsonPath("$.outcomeAccountNumber", is("PL48234567840000000000000031")))
        .andExpect(jsonPath("$.incomeAccountNumber", is("PL48234567840000000000000032")))
        .andExpect(jsonPath("$.type", is("INCOME")))
        .andExpect(jsonPath("$.transactionDate", is(date)))
        .andExpect(jsonPath("$.transactionTime", notNullValue(String.class)));
  }

  @Test
  @WithUserId()
  void whenGetDetailedTransactionWhenIncomeApproved_thenSuccess() throws Exception {
    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    String date = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(zonedDateTime);

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL + "/" + TRANSACTION_ID_CASE_7)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(7)))
        .andExpect(jsonPath("$.transactionAmount", is(700)))
        .andExpect(jsonPath("$.currency", is("PLN")))
        .andExpect(jsonPath("$.status", is("APPROVED")))
        .andExpect(jsonPath("$.transactionName", is("transaction7")))
        .andExpect(jsonPath("$.outcomeAccountNumber", is("PL48234567840000000000000031")))
        .andExpect(jsonPath("$.incomeAccountNumber", is("PL48234567840000000000000011")))
        .andExpect(jsonPath("$.type", is("INCOME")))
        .andExpect(jsonPath("$.transactionDate", is(date)))
        .andExpect(jsonPath("$.transactionTime", notNullValue(String.class)));
  }

  @Test
  @WithUserId(SENDER_ID)
  void whenGetDetailedTransactionWhenOutcomeApproved_thenSuccess() throws Exception {
    ZonedDateTime zonedDateTime = ZonedDateTime.now();
    String date = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(zonedDateTime);

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL + "/" + TRANSACTION_ID_CASE_7)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(7)))
        .andExpect(jsonPath("$.transactionAmount", is(700)))
        .andExpect(jsonPath("$.currency", is("PLN")))
        .andExpect(jsonPath("$.status", is("APPROVED")))
        .andExpect(jsonPath("$.transactionName", is("transaction7")))
        .andExpect(jsonPath("$.outcomeAccountNumber", is("PL48234567840000000000000031")))
        .andExpect(jsonPath("$.incomeAccountNumber", is("PL48234567840000000000000011")))
        .andExpect(jsonPath("$.type", is("OUTCOME")))
        .andExpect(jsonPath("$.transactionDate", is(date)))
        .andExpect(jsonPath("$.transactionTime", notNullValue(String.class)));
  }
}
