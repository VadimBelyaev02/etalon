package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.DocumentController.BANK_INFO_DOCUMENT_URL;
import static com.andersenlab.etalon.infoservice.controller.DocumentController.TRANSFERS_RECEIPTS_URI;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

class DownloadTransferReceiptComponentTest extends AbstractComponentTest {
  private static final String VALID_TRANSACTION_ID = "123";
  private static final String VALID_USER_ID = "user";
  private static final String INVALID_USER_ID = "invalidUser";
  private static final String DECODED_USER_ERROR_MESSAGE =
      "User with such credentials (%s) does not exist".formatted(INVALID_USER_ID);

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadReceiptPdf_shouldSuccess() throws Exception {
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSFERS_RECEIPTS_URI)
                    .toUriString())
                .param("transferId", VALID_TRANSACTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Pragma", "no-cache"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }
}
