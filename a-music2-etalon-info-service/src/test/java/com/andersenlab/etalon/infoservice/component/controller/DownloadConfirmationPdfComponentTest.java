package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.controller.DocumentController.BANK_INFO_DOCUMENT_URL;
import static com.andersenlab.etalon.infoservice.controller.DocumentController.CONFIRMATION_STATEMENT_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

class DownloadConfirmationPdfComponentTest extends AbstractComponentTest {

  private static final String VALID_TRANSACTION_ID = "123";
  private static final String INVALID_TRANSACTION_ID = "911";
  private static final String VALID_USER_ID = "user";
  private static final String INVALID_USER_ID = "invalidUser";
  private static final String DECODED_USER_ERROR_MESSAGE =
      "User with such credentials (%s) does not exist".formatted(INVALID_USER_ID);
  private static final String DECODED_TRANSACTION_ERROR_MESSAGE =
      "Detailed transaction view has been rejected because given accounts in this transaction doesn't belong to this user";
  private static final String DECODED_UNSUPPORTED_LOCALE_ERROR_MESSAGE =
      "This locale is not supported";

  @BeforeEach
  void setUp() {}

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationPdfWithUnsupportedLocale_shouldFail() throws Exception {
    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + CONFIRMATION_STATEMENT_URI)
                    .toUriString())
                .param("transactionId", VALID_TRANSACTION_ID)
                .param("locale", "unknown")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(DECODED_UNSUPPORTED_LOCALE_ERROR_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadPolishConfirmationPdf_shouldSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + CONFIRMATION_STATEMENT_URI)
                    .toUriString())
                .param("transactionId", VALID_TRANSACTION_ID)
                .param("locale", "pl")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Pragma", "no-cache"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationPdf_shouldSuccess() throws Exception {
    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + CONFIRMATION_STATEMENT_URI)
                    .toUriString())
                .param("transactionId", VALID_TRANSACTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Pragma", "no-cache"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }

  @Test
  @WithUserId(INVALID_USER_ID)
  void whenDownloadConfirmationWithInvalidUserId_shouldHandleFeignException() throws Exception {

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + CONFIRMATION_STATEMENT_URI)
                    .toUriString())
                .param("transactionId", VALID_TRANSACTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(DECODED_USER_ERROR_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationWithInvalidAccountNumber_shouldHandleFeignException()
      throws Exception {

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + CONFIRMATION_STATEMENT_URI)
                    .toUriString())
                .param("transactionId", INVALID_TRANSACTION_ID)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(DECODED_TRANSACTION_ERROR_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void shouldIncludeCurrencyInPdfResponse() throws Exception {
    byte[] pdfBytes =
        mockMvc
            .perform(
                get(UriComponentsBuilder.fromPath(
                            BANK_INFO_DOCUMENT_URL + CONFIRMATION_STATEMENT_URI)
                        .toUriString())
                    .param("transactionId", VALID_TRANSACTION_ID)
                    .param("locale", "en")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
            .andExpect(header().string("Pragma", "no-cache"))
            .andExpect(header().string("Expires", "0"))
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

    try (PDDocument document = PDDocument.load(pdfBytes)) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      String pdfText = pdfStripper.getText(document);

      assertThat(pdfText).contains("PLN");
    }
  }
}
