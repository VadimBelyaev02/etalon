package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.util.Constants.MONTH_GAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.controller.TransactionController;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class GetAllTransactionsForAccountsEndpointTest extends AbstractIntegrationTest {

  @BeforeEach
  void setUp() {}

  @Test
  void whenGetAllTransactionsForAccounts_thenSuccess() throws Exception {
    List<String> values = List.of("PL48234567840000000000000031");
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.addAll("accountNumber", values);

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL + TransactionController.ACCOUNTS_URI)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(4)))
        .andExpect(jsonPath("$[0].amount", is(10000)))
        .andExpect(jsonPath("$[0].currency", is("PLN")))
        .andExpect(jsonPath("$[0].status", is("APPROVED")))
        .andExpect(jsonPath("$[0].name", is("transaction4")))
        .andExpect(jsonPath("$[0].accountNumber", is("PL48234567840000000000000031")))
        .andExpect(jsonPath("$[0].type", is("OUTCOME")));
  }

  @Test
  void whenGetAllTransactionsForAccountsEmptyParams_thenSuccess() throws Exception {
    List<String> values = List.of("");
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.addAll("accountNumber", values);

    mockMvc
        .perform(
            get(TransactionController.TRANSACTIONS_V1_URL + TransactionController.ACCOUNTS_URI)
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().string("[]"));
  }

  @Test
  void whenGetTimeframeTransactionsForAccounts_shouldReturnOnlyForSixMonths() throws Exception {
    List<String> values = List.of("PL48234567840000000000000031");
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.addAll("accountNumber", values);

    MvcResult result =
        mockMvc
            .perform(
                get(TransactionController.TRANSACTIONS_V1_URL + TransactionController.ACCOUNTS_URI)
                    .params(requestParams)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();

    Integer size = JsonPath.read(result.getResponse().getContentAsString(), "$.size()");
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime start = now.minusMonths(MONTH_GAP);
    for (int i = 0; i < size; i++) {
      ZonedDateTime createdAt =
          ZonedDateTime.parse(
              JsonPath.read(result.getResponse().getContentAsString(), "$[" + i + "].createAt"));
      assertThat(createdAt).isBefore(now);
      assertThat(createdAt).isAfter(start);
    }
  }
}
