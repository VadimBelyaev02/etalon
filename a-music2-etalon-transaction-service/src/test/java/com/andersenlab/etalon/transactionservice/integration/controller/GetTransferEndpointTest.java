package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidTransferResponseDto;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransferController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

public class GetTransferEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "3";
  public static final String INVALID_USER_ID = "2";
  public static final String PROCESSING_TRANSFER_ID = "1";
  public static final String TRANSFER_ID = "/2";
  public static final String DECLINED_TRANSFER_ID = "3";

  @Test
  @WithUserId(USER_ID)
  void whenGetTransfersWithCorrectUser_thenSuccess() throws Exception {
    TransferResponseDto expected = getValidTransferResponseDto();

    mockMvc
        .perform(
            get(TransferController.TRANSFERS_V1_URL + TRANSFER_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.id", is(expected.id().intValue())))
        .andExpect(jsonPath("$.description", is(expected.description())))
        .andExpect(jsonPath("$.amount", is(expected.amount().doubleValue())))
        .andExpect(jsonPath("$.totalAmount", is(expected.totalAmount().doubleValue())))
        .andExpect(jsonPath("$.fee", is(expected.fee().doubleValue())))
        .andExpect(jsonPath("$.beneficiaryAccountNumber", is(expected.beneficiaryAccountNumber())))
        .andExpect(jsonPath("$.senderAccountNumber", is(expected.senderAccountNumber())))
        .andExpect(jsonPath("$.senderFullName", is(expected.senderFullName())))
        .andExpect(jsonPath("$.beneficiaryBank", is(expected.beneficiaryBank())))
        .andExpect(jsonPath("$.beneficiaryFullName", is(expected.beneficiaryFullName())))
        .andExpect(jsonPath("$.status", is(expected.status().name())))
        .andExpect(jsonPath("$.createAt", is(expected.createAt().toString())))
        .andExpect(jsonPath("$.updateAt", is(expected.updateAt().toString())));
  }

  @Test
  @WithUserId(INVALID_USER_ID)
  void whenGetTransfersWithIncorrectUser_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            get(TransferController.TRANSFERS_V1_URL + TRANSFER_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY)));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetTransfersWithProcessingStatus_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            get(
                UriComponentsBuilder.fromPath(TransferController.TRANSFERS_V1_URL)
                    .pathSegment(PROCESSING_TRANSFER_ID)
                    .toUriString()))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.message",
                is(BusinessException.TRANSFER_IS_PROCESSING.formatted(PROCESSING_TRANSFER_ID))));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetTransfersWithDeclinedStatus_thenBadRequest() throws Exception {
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(TransferController.TRANSFERS_V1_URL)
                    .pathSegment(DECLINED_TRANSFER_ID)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.message",
                is(BusinessException.TRANSFER_IS_DECLINED.formatted(DECLINED_TRANSFER_ID))));
  }
}
