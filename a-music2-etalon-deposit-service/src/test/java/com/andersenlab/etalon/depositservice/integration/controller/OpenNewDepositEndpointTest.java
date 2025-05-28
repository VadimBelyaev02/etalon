package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.controller.DepositController;
import com.andersenlab.etalon.depositservice.dto.deposit.request.OpenDepositRequestDto;
import com.andersenlab.etalon.depositservice.exception.BusinessException;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

class OpenNewDepositEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID_WITH_OPEN_DEPOSIT = "2";
  public static final String USER_ID_WITH_OUT_OPEN_DEPOSIT = "wrong_user";
  private OpenDepositRequestDto openDepositRequestDto;
  private OpenDepositRequestDto excessAmountRequestDto;
  private OpenDepositRequestDto lessPeriodRequestDto;
  private OpenDepositRequestDto morePeriodRequestDto;

  @BeforeEach
  void setUp() {
    OpenDepositRequestDto requestDto =
        OpenDepositRequestDto.builder()
            .depositProductId(2L)
            .sourceAccount("PL04234567840000000000000001")
            .interestAccount("PL04234567840000000000000001")
            .finalTransferAccount("PL04234567840000000000000001")
            .build();

    openDepositRequestDto =
        requestDto.toBuilder().depositAmount(BigDecimal.valueOf(1000)).depositPeriod(12).build();

    excessAmountRequestDto =
        requestDto.toBuilder().depositAmount(BigDecimal.valueOf(9000)).depositPeriod(12).build();

    lessPeriodRequestDto =
        requestDto.toBuilder().depositAmount(BigDecimal.valueOf(9000)).depositPeriod(5).build();

    morePeriodRequestDto =
        requestDto.toBuilder().depositAmount(BigDecimal.valueOf(9000)).depositPeriod(25).build();
  }

  @Test
  void whenOpenNewDeposit_shouldReturnConfirmationRequest() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(openDepositRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.confirmationId", notNullValue()));
  }

  @Test
  void shouldFailOpenNewDeposit_WhenSourceAccountOrProfitAccountDoesNotBelongToUser()
      throws Exception {
    String expectedErrorMessage = BusinessException.OPERATION_REJECTED_INVALID_ACCOUNT_NUMBER;
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OUT_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(openDepositRequestDto)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.message", is(expectedErrorMessage)));
  }

  @Test
  void shouldFailOpenNewDeposit_WhenInsufficientFundsInSourceAccount() throws Exception {
    String expectedErrorMessage =
        "Transaction for user has been rejected due to insufficient balance";
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(excessAmountRequestDto)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(expectedErrorMessage)));
  }

  @Test
  void shouldFailOpenNewDeposit_WhenPeriodLessProductPeriod() throws Exception {
    String expectedErrorMessage = "Value is less than the minimum required";
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(lessPeriodRequestDto)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(expectedErrorMessage)));
  }

  @Test
  void shouldFailOpenNewDeposit_WhenPeriodMoreProductPeriod() throws Exception {
    String expectedErrorMessage = "Value is more than the maximum required";
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(DepositController.DEPOSITS_V1_URL).toUriString())
                .header("authenticated-user-id", USER_ID_WITH_OPEN_DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(morePeriodRequestDto)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(expectedErrorMessage)));
  }
}
