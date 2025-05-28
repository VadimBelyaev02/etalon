package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.controller.DepositOrderController;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(
    scripts = "file:src/test/resources/data/deposit-orders-initial-data.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ConfirmOpeningNewDepositEndpointTest extends AbstractIntegrationTest {
  public static final String USER_ID_WITH_OPEN_DEPOSIT = "user";
  public static final Long VALID_DEPOSIT_ORDER_ID = 1L;
  public static final Long INVALID_DEPOSIT_ORDER_ID = 101L;

  @Test
  void shouldReturnMessage_WhenConfirmationSuccessful() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositOrderController.DEPOSIT_ORDER_URL
                            + "/"
                            + VALID_DEPOSIT_ORDER_ID
                            + DepositOrderController.DEPOSIT_ORDER_CONFIRM)
                    .toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is("Deposit processing in progress")));
  }

  @Test
  void shouldFail_WhenInvalidConfirmationId() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        DepositOrderController.DEPOSIT_ORDER_URL
                            + "/"
                            + INVALID_DEPOSIT_ORDER_ID
                            + DepositOrderController.DEPOSIT_ORDER_CONFIRM)
                    .toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
  }
}
