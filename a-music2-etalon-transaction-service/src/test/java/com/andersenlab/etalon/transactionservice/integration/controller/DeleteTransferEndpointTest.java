package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.TransferController;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class DeleteTransferEndpointTest extends AbstractIntegrationTest {

  @Test
  @WithUserId("3")
  void deleteTransfer_ShouldReturnOk_WhenTransferIsDeleted() throws Exception {
    long transferId = 1L;
    mockMvc
        .perform(delete(TransferController.TRANSFERS_ID_URL, transferId))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserId("user")
  void deleteTransfer_ShouldReturnForbidden_WhenTransferDoesNotBelongToUser() throws Exception {
    long transferId = 1L;
    mockMvc
        .perform(delete(TransferController.TRANSFERS_ID_URL, transferId))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUserId("3")
  void deleteTransfer_ShouldReturnConflict_WhenTransferIsNotInProcessingStatus() throws Exception {
    long transferId = 2L;
    mockMvc
        .perform(delete(TransferController.TRANSFERS_ID_URL, transferId))
        .andExpect(status().isConflict());
  }

  @Test
  @WithUserId("3")
  void deleteTransfer_ShouldReturnNotFound_WhenTransferDoesNotExist() throws Exception {
    long transferId = 111L;
    mockMvc
        .perform(delete(TransferController.TRANSFERS_ID_URL, transferId))
        .andExpect(status().isNotFound());
  }
}
