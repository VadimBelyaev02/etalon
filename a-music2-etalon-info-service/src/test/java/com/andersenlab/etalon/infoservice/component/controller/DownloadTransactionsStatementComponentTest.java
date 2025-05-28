package com.andersenlab.etalon.infoservice.component.controller;

import static com.andersenlab.etalon.infoservice.MockData.getValidTransactionFilter;
import static com.andersenlab.etalon.infoservice.controller.DocumentController.BANK_INFO_DOCUMENT_URL;
import static com.andersenlab.etalon.infoservice.controller.DocumentController.TRANSACTIONS_STATEMENT_URI;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.infoservice.annotation.WithUserId;
import com.andersenlab.etalon.infoservice.component.AbstractComponentTest;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

class DownloadTransactionsStatementComponentTest extends AbstractComponentTest {
  private static final String VALID_USER_ID = "user";
  private static final String INVALID_USER_ID = "invalidUser";
  private static final String PDF_STATEMENT = "pdf";
  private static final String DECODED_USER_ERROR_MESSAGE =
      "User with such credentials (%s) does not exist".formatted(INVALID_USER_ID);
  private static final String DECODED_TRANSACTION_ERROR_MESSAGE =
      "View transactions for account has been rejected because given account number doesn't belong to this user";
  private static final String DECODED_UNSUPPORTED_LOCALE_ERROR_MESSAGE =
      "This locale is not supported";

  @BeforeEach
  void setUp() {}

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationExcelWithUnsupportedLocale_shouldFail() throws Exception {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("statementType", "xlsx");
    requestParams.add("startDate", "2023-10-21T11:00:00Z");
    requestParams.add("endDate", "2023-10-21T11:00:00Z");
    requestParams.add("sortBy", "createAt");
    requestParams.add("orderBy", "asc");
    requestParams.add("locale", "unknown");
    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSACTIONS_STATEMENT_URI)
                    .toUriString())
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(DECODED_UNSUPPORTED_LOCALE_ERROR_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationExcel_shouldSuccess() throws Exception {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("statementType", "xlsx");
    requestParams.add("startDate", "2023-10-21T11:00:00Z");
    requestParams.add("endDate", "2023-10-21T11:00:00Z");
    requestParams.add("sortBy", "createAt");
    requestParams.add("orderBy", "asc");

    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSACTIONS_STATEMENT_URI)
                    .toUriString())
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Pragma", "no-cache"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }

  @ParameterizedTest
  @MethodSource("downloadTransactionStatementsWithCorrectTypeTestDataSource")
  @WithUserId(VALID_USER_ID)
  void whenDownloadTransactionStatementsWithCorrectTypeAndAccountNumber_shouldSuccess(
      TransactionFilter transactionFilter) throws Exception {
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSACTIONS_STATEMENT_URI)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON)
                .param("statementType", PDF_STATEMENT)
                .param("type", transactionFilter.getType())
                .param("startDate", transactionFilter.getStartDate())
                .param("endDate", transactionFilter.getEndDate())
                .param("sortBy", transactionFilter.getSortBy())
                .param("orderBy", transactionFilter.getOrderBy()))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Pragma", "no-cache"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
  }

  static Stream<TransactionFilter> downloadTransactionStatementsWithCorrectTypeTestDataSource() {
    return Stream.of(
        getValidTransactionFilter(),
        getValidTransactionFilter().toBuilder().accountNumber("all").build(),
        getValidTransactionFilter().toBuilder().type("All").build(),
        getValidTransactionFilter().toBuilder().type("ALL").accountNumber("All").build());
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationPdf_shouldSuccess() throws Exception {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("statementType", "pdf");
    requestParams.add("startDate", "2023-10-21T11:00:00Z");
    requestParams.add("endDate", "2023-10-21T11:00:00Z");
    requestParams.add("sortBy", "createAt");
    requestParams.add("orderBy", "asc");

    // when/then
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSACTIONS_STATEMENT_URI)
                    .toUriString())
                .params(requestParams)
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
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("statementType", "pdf");
    requestParams.add("startDate", "2023-10-21T11:00:00Z");
    requestParams.add("endDate", "2023-10-21T11:00:00Z");
    requestParams.add("sortBy", "createAt");
    requestParams.add("orderBy", "asc");

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSACTIONS_STATEMENT_URI)
                    .toUriString())
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(DECODED_USER_ERROR_MESSAGE)));
  }

  @Test
  @WithUserId(VALID_USER_ID)
  void whenDownloadConfirmationWithInvalidAccountNumber_shouldHandleFeignException()
      throws Exception {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("accountNumber", "invalidAccountNumber");
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(BANK_INFO_DOCUMENT_URL + TRANSACTIONS_STATEMENT_URI)
                    .toUriString())
                .params(requestParams)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(DECODED_TRANSACTION_ERROR_MESSAGE)));
  }
}
