package com.andersenlab.etalon.depositservice.integration.controller;

import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(
    scripts = {
      "classpath:data/deposit-products-initial-data.sql",
      "classpath:data/deposit-initial-data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GetFilteredDepositsEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenGetFilteredDeposits_shouldReturnCorrectUserDeposit() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V2_URL).toUriString())
                .header("authenticated-user-id", "2")
                .param("accountNumber", "PL04234567840000000000000002")
                .param("pageNo", "0")
                .param("pageSize", "6")
                .param("sortBy", "status")
                .param("orderBy", "asc"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.is(6)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", Matchers.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", Matchers.is(6)))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.content[*].duration", Matchers.everyItem(Matchers.is(12))))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].status", Matchers.is("ACTIVE")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].actualAmount", Matchers.is(9000.0)))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.content[0].endDate", Matchers.is("2024-06-20T12:01:37Z")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[5].status", Matchers.is("EXPIRED")));
  }

  @Test
  void whenGetFilteredDepositsByStatuses_shouldReturnCorrectUserDeposit() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V2_URL).toUriString())
                .header("authenticated-user-id", "2")
                .param("statusList", "ACTIVE"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.is(5)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", Matchers.is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", Matchers.is(5)))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.content[*].status", Matchers.everyItem(Matchers.is("ACTIVE"))));
  }

  @Test
  void whenGetFilteredDepositsWithNonLinkedAccount_shouldReturnEmptyList() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V2_URL).toUriString())
                .header("authenticated-user-id", "2")
                .param("accountNumber", "PL04234567840000000000000012"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.is(0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", Matchers.is(0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", Matchers.is(0)));
  }

  // Negative test cases

  @Test
  void whenGetFilteredDepositsWithInvalidStatus_shouldReturnBadRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V2_URL).toUriString())
                .header("authenticated-user-id", "2")
                .param("statusList", "f"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  void whenInvalidPageNo_shouldReturnBadRequest() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v2/deposits").param("pageNo", "-1"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message", Matchers.is("Page number must be 0 or greater")));
  }

  @Test
  void whenGetFilteredDepositsWithInvalidPageSize_shouldReturnBadRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V2_URL).toUriString())
                .header("authenticated-user-id", "2")
                .param("accountNumber", "PL04234567840000000000000002")
                .param("pageNo", "0")
                .param("pageSize", "0")
                .param("sortBy", "status")
                .param("orderBy", "asc"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message", Matchers.is("Page size must be 1 or greater")));
  }

  @Test
  void whenGetFilteredDepositsWithInvalidSortBy_shouldReturnBadRequest() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V2_URL).toUriString())
                .header("authenticated-user-id", "2")
                .param("accountNumber", "PL04234567840000000000000002")
                .param("pageNo", "0")
                .param("pageSize", "6")
                .param("sortBy", "invalidSortBy")
                .param("orderBy", "asc"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.is("Invalid field name")));
  }
}
