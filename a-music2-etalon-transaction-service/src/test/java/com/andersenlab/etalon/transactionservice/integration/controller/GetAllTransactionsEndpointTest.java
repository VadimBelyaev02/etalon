package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransactionController;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class GetAllTransactionsEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "1";

  @BeforeEach
  void setUp() {}

  @Test
  @WithUserId
  void whenGetAllTransactionsWithoutParams_thenSuccess() throws Exception {

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL)
                .param("accountNumber", "PL48234567840000000000000011")
                .param("accountNumber", "PL48234567840000000000000012")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(7)))
        .andExpect(jsonPath("$[0].amount", is(700)))
        .andExpect(jsonPath("$[0].currency", is("PLN")))
        .andExpect(jsonPath("$[0].status", is("APPROVED")))
        .andExpect(jsonPath("$[0].name", is("transaction7")))
        .andExpect(jsonPath("$[0].accountNumber", is("PL48234567840000000000000011")))
        .andExpect(jsonPath("$[0].type", is("INCOME")))
        .andExpect(jsonPath("$[0].eventType", is("BODY")));
  }

  @Test
  @WithUserId("7")
  void whenGetAllExtendedTransactionsWithoutParams_thenSuccess() throws Exception {
    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V2_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.content.[0].id").value(15))
        .andExpect(jsonPath("$.content.[0].events").doesNotExist());
  }

  @Test
  @WithUserId("7")
  void whenGetAllExtendedTransactionsWithEvents_thenSuccess() throws Exception {
    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V2_URL)
                .param("withEvents", String.valueOf(true))
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4)))
        .andExpect(jsonPath("$.content.[0].id").value(15))
        .andExpect(jsonPath("$.content.[1].events").exists())
        .andExpect(jsonPath("$.content.[1].events", hasSize(3)))
        .andExpect(
            jsonPath(
                    "$.content[1].events.[?(@.type == 'INCOME' && @.amount == '300' && @.accountNumber=='PL48234567840000000000009741')]")
                .exists())
        .andExpect(
            jsonPath(
                    "$.content[1].events.[?(@.type == 'OUTCOME' && @.amount == '300' && @.accountNumber=='PL48234567840000000000004152')]")
                .exists())
        .andExpect(
            jsonPath(
                    "$.content[1].events.[?(@.type == 'OUTCOME' && @.amount == '3' && @.accountNumber=='PL48234567840000000000004152')]")
                .exists());
  }

  @Test
  @WithUserId("7")
  void whenGetAllTransactions() throws Exception {
    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V2_URL)
                .param("type", "income")
                .param("transactionStatus", "approved")
                .param("transactionGroup", "transfer")
                .param("amountFrom", "100")
                .param("amountTo", "200")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content.[0].totalAmount", is(100)))
        .andExpect(jsonPath("$.content.[0].accountNumber", is("PL48234567840000000000009741")))
        .andExpect(jsonPath("$.content.[0].shortCardInfo.id", is(4)));
  }

  @Test
  @WithUserId
  void whenGetAllTransactionsWithAccountNumber_thenSuccess() throws Exception {

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL)
                .param("accountNumber", "PL48234567840000000000000011")
                .param("accountNumber", "PL48234567840000000000000012")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(7)))
        .andExpect(jsonPath("$[0].amount", is(700)))
        .andExpect(jsonPath("$[0].currency", is("PLN")))
        .andExpect(jsonPath("$[0].status", is("APPROVED")))
        .andExpect(jsonPath("$[0].type", is("INCOME")))
        .andExpect(jsonPath("$[0].eventType", is("BODY")));
  }

  @Test
  @WithUserId
  void whenGetAllTransactionsWithFilters_thenSuccess() throws Exception {

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL)
                .param("startDate", ZonedDateTime.now().minusDays(2).toString())
                .param("endDate", ZonedDateTime.now().toString())
                .param("transactionStatus", "approved")
                .param("transactionGroup", "transfer")
                .param("amountFrom", "500")
                .param("amountTo", "800")
                .param("sortBy", "amount")
                .param("orderBy", "asc")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(7)))
        .andExpect(jsonPath("$[0].amount", is(700)))
        .andExpect(jsonPath("$[0].currency", is("PLN")))
        .andExpect(jsonPath("$[0].status", is("APPROVED")))
        .andExpect(jsonPath("$[0].type", is("INCOME")))
        .andExpect(jsonPath("$[0].eventType", is("BODY")))
        .andExpect(jsonPath("$.*", hasSize(2)));
  }

  @Test
  @WithUserId
  void whenGetAllTransactionsWithFiltersType_thenSuccess() throws Exception {

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL)
                .param("startDate", ZonedDateTime.now().minusDays(2).toString())
                .param("endDate", ZonedDateTime.now().toString())
                .param("type", "income")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(7)))
        .andExpect(jsonPath("$[0].amount", is(700)))
        .andExpect(jsonPath("$[0].currency", is("PLN")))
        .andExpect(jsonPath("$[0].status", is("APPROVED")))
        .andExpect(jsonPath("$[0].type", is("INCOME")))
        .andExpect(jsonPath("$[0].eventType", is("BODY")));
  }

  @Test
  @WithUserId("2")
  void whenGetAllTransactionsUserWithWrongId_thenFail() throws Exception {

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL)
                .param("accountNumber", "2")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath(
                "$.message", is(BusinessException.VIEW_TRANSACTIONS_REJECTED_DUE_TO_SECURITY)));
  }
}
