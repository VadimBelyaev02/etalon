package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.MockData;
import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositReplenishRequestDto;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(
    scripts = {
      "classpath:data/deposit-products-initial-data.sql",
      "classpath:data/deposit-initial-data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReplenishDepositEndpointTest extends AbstractIntegrationTest {

  private static final String DEPOSIT_ID = "/1";
  private static final String USER_ID = "2";

  @Test
  void whenGetReplenished_shouldThrowExceptionInsufficientFunds() throws Exception {
    DepositReplenishRequestDto dto =
        MockData.getValidDepositReplenishRequestDto().toBuilder()
            .replenishAmount(BigDecimal.valueOf(100_000))
            .withdrawalAccountNumber("1")
            .build();

    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL
                            + DEPOSIT_ID
                            + DepositController.REPLENISHED_URI)
                    .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath(
                "$.message", is(BusinessException.DEPOSIT_REPLENISH_REJECTED_INSUFFICIENT_FUNDS)));
  }

  @Test
  void whenGetReplenished_shouldThrowExceptionInvalidAccountNumber() throws Exception {
    DepositReplenishRequestDto dto =
        MockData.getValidDepositReplenishRequestDto().toBuilder()
            .withdrawalAccountNumber("5")
            .build();
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL
                            + DEPOSIT_ID
                            + DepositController.REPLENISHED_URI)
                    .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(
            jsonPath("$.message", is(BusinessException.OPERATION_REJECTED_INVALID_ACCOUNT_NUMBER)));
  }

  @Test
  void whenGetReplenished_shouldPass() throws Exception {
    DepositReplenishRequestDto dto =
        MockData.getValidDepositReplenishRequestDto().toBuilder()
            .replenishAmount(BigDecimal.valueOf(100))
            .withdrawalAccountNumber("1")
            .build();

    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL
                            + DEPOSIT_ID
                            + DepositController.REPLENISHED_URI)
                    .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()));
  }

  @Test
  void whenGetReplenished_shouldThrowExceptionAccountBlocked() throws Exception {
    DepositReplenishRequestDto dto =
        MockData.getValidDepositReplenishRequestDto().toBuilder()
            .withdrawalAccountNumber("2")
            .build();
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositController.DEPOSITS_V1_URL
                            + DEPOSIT_ID
                            + DepositController.REPLENISHED_URI)
                    .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath("$.message", is(BusinessException.TRANSACTION_REJECTED_ACCOUNT_BLOCKED)));
  }
}
