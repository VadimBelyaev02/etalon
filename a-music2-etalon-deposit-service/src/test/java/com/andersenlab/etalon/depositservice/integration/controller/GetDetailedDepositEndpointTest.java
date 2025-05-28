package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import java.util.List;
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
class GetDetailedDepositEndpointTest extends AbstractIntegrationTest {

  public static final String DEPOSIT_ID = "1";
  public static final String WRONG_DEPOSIT_ID = "20";

  @Test
  void whenGetDetailedDeposit_shouldSuccess() throws Exception {

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + "/" + DEPOSIT_ID)
                    .toUriString())
                .header("authenticated-user-id", "2"))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.productName", is("Looong")))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.accountNumber", is("PL04234567840000000000000001")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.actualAmount", is(9000.0)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.productCurrency", is("PLN")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.validFrom", is("2023-06-20T12:01:37Z")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.validUntil", is("2024-06-20T12:01:37Z")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.productInterestRate", is(7)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status", is("ACTIVE")))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.interestAccountNumber", is("PL04234567840000000000000002")))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.finalTransferAccountNumber", is("PL04234567840000000000000002")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.isProductEarlyWithdrawal", is(true)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyPayments", is(List.of())));
  }

  @Test
  void whenGetDetailedDepositWithWrongId_shouldFail() throws Exception {

    String expectedErrorMessage =
        String.format(BusinessException.DEPOSIT_NOT_FOUND_BY_ID, WRONG_DEPOSIT_ID);
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL + "/" + WRONG_DEPOSIT_ID)
                    .toUriString())
                .header("authenticated-user-id", "2"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.message", is(expectedErrorMessage)));
  }
}
