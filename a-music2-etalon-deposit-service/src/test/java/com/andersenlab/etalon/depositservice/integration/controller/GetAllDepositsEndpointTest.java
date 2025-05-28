package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(
    scripts = {
      "classpath:data/deposit-products-initial-data.sql",
      "classpath:data/deposit-initial-data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GetAllDepositsEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenGetAllDeposits_shouldReturnCorrectUserDeposit() throws Exception {
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", "4"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].status", is("ACTIVE")))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", is(6)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].duration", is(6)))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].endDate", is("2023-11-30T12:01:37Z")));
  }

  @Test
  void whenGetAllDeposits_shouldReturnCorrectNumberOfUserDepositsWithDefaultPageNoAndPageSize()
      throws Exception {

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", "2"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$", hasSize(3)));
  }

  @Test
  void whenGetAllDeposits_shouldReturnEmptyListWhenNoOpenUserDeposits() throws Exception {

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", "10"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().string("[]"));
  }
}
