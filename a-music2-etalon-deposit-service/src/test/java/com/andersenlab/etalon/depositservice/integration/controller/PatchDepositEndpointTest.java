package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.dto.deposit.request.DepositUpdateRequestDto;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
class PatchDepositEndpointTest extends AbstractIntegrationTest {

  private static final String DEPOSIT_ID = "/1";
  private static final String USER_ID = "2";

  @Test
  void whenPatchDepositWithOneParameter_shouldPass() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000005")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                        .toUriString())
                .header("authenticated-user-id", USER_ID))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.interestAccountNumber", is("PL04234567840000000000000005")))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.finalTransferAccountNumber", is("PL04234567840000000000000002")));
  }

  @Test
  void whenPatchDepositWithTwoParameters_shouldPass() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000005")
            .finalTransferAccountNumber("PL04234567840000000000000005")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                    .toUriString())
                .header("authenticated-user-id", USER_ID))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.interestAccountNumber", is("PL04234567840000000000000005")))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.finalTransferAccountNumber", is("PL04234567840000000000000005")));
  }

  @Test
  void whenUpdateDepositWithNonExistingDepositId_shouldReturnNotFound() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000005")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + "/999")
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message", Matchers.is("Cannot find deposit with id 999")));
  }

  @Test
  void whenUpdateDepositWithNonExistingAccountNumber_shouldReturnNotFound() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000009")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + "/999")
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.is(
                    "Cannot find account entity with account number PL04234567840000000000000009")));
  }

  @Test
  void whenUpdateDepositWithoutProvidedFields_shouldReturnBadRequest() throws Exception {
    DepositUpdateRequestDto dto = DepositUpdateRequestDto.builder().build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.is(
                    "At least one of the fields must be provided: interestAccountNumber, finalTransferAccountNumber")));
  }

  @ParameterizedTest
  @CsvSource({
    "PL0423456784000 0000000000007",
    "РL04234567840000000000000007",
    "' РL04234567840000000000000007'",
    "123",
    " \"!@*&%\" ",
  })
  void whenUpdateDepositWithInvalidAccountNumber_shouldReturnBadRequest(
      final String providedAccountNumber) throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder().interestAccountNumber(providedAccountNumber).build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.is(
                    "IBAN should start with two letters followed by 26 digits, 28 characters in total")));
  }

  @Test
  void whenUpdateDepositWithNotUserAccount_shouldReturnBadRequest() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000006")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + DEPOSIT_ID)
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.is(
                    "Operation for user has been rejected because given account number is invalid")));
  }

  @Test
  void whenUpdateDepositWithBlockedAccount_shouldReturnBadRequest() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000007")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + "/2")
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.is(
                    "Transaction for user has been rejected because given account is blocked")));
  }

  @Test
  void whenUpdateDepositWithSameAccount_shouldReturnBadRequest() throws Exception {
    DepositUpdateRequestDto dto =
        DepositUpdateRequestDto.builder()
            .interestAccountNumber("PL04234567840000000000000002")
            .build();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch(
                    UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL + "/2")
                        .toUriString())
                .header("authenticated-user-id", USER_ID)
                .content(toJson(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.message",
                Matchers.is("Deposit update rejected due to the same account number provided")));
  }
}
