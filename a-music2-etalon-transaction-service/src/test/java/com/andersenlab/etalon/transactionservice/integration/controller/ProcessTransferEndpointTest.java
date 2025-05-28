package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.exception.BusinessException.DAILY_EXPENSES_LIMIT_EXCEEDED;
import static com.andersenlab.etalon.transactionservice.exception.BusinessException.LIMIT_OF_TRANSFER_AMOUNT_EXCEEDED;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransferController;
import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

public class ProcessTransferEndpointTest extends AbstractIntegrationTest {
  public static final String USER_ID = "1L";
  public static final Long VALID_TRANSFER_ID = 1L;

  @Test
  @WithUserId(USER_ID)
  void shouldReturnMessage_WhenConfirmationSuccessful() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.API_V2_URI
                            + TransferController.TRANSFERS_URI
                            + "/"
                            + VALID_TRANSFER_ID
                            + TransferController.CONFIRMED_URL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message", is(MessageResponseDto.OPERATION_IS_PROCESSING)));
  }

  @Test
  @WithUserId(USER_ID)
  void shouldFail_WhenInvalidTransferId() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.API_V2_URI
                            + TransferController.TRANSFERS_URI
                            + "/"
                            + VALID_TRANSFER_ID
                            + 10
                            + TransferController.CONFIRMED_URL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithUserId(USER_ID)
  void shouldReturnMessage_WhenConfirmationSuccessfulV2() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.API_V2_URI
                            + TransferController.TRANSFERS_URI
                            + "/"
                            + VALID_TRANSFER_ID
                            + TransferController.CONFIRMED_URL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message", is(MessageResponseDto.OPERATION_IS_PROCESSING)));
  }

  @Test
  @WithUserId(USER_ID)
  void shouldFail_WhenInvalidTransferIdV2() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.TRANSFERS_V2_URL
                            + VALID_TRANSFER_ID
                            + 10
                            + TransferController.TRANSFER_CONFIRM_URI)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithUserId(USER_ID)
  void shouldFail_WhenConfirmationSuccessfulAndTransferAmountExceeded() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.API_V2_URI
                            + TransferController.TRANSFERS_URI
                            + "/"
                            + "8"
                            + TransferController.CONFIRMED_URL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value(LIMIT_OF_TRANSFER_AMOUNT_EXCEEDED.formatted(500)));
  }

  @Test
  @WithUserId(USER_ID)
  void shouldFail_WhenConfirmationSuccessfulAndExpensesAmountPerDayExceeded() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.API_V2_URI
                            + TransferController.TRANSFERS_URI
                            + "/"
                            + "9"
                            + TransferController.CONFIRMED_URL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value(DAILY_EXPENSES_LIMIT_EXCEEDED));
  }

  @Test
  @WithUserId(USER_ID)
  void shouldFail_WhenConfirmationSuccessfulAndInsufficientFunds() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        TransferController.API_V2_URI
                            + TransferController.TRANSFERS_URI
                            + "/"
                            + "10"
                            + TransferController.CONFIRMED_URL)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.message")
                .value(
                    String.format(
                        BusinessException.NOT_ENOUGH_FUNDS_ON_ACCOUNT,
                        "PL48234567840000000000000012")));
  }
}
