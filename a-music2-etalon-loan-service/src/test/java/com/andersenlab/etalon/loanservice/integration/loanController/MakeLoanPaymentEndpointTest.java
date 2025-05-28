package com.andersenlab.etalon.loanservice.integration.loanController;

import static com.andersenlab.etalon.loanservice.controller.LoanController.LOANS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.ActiveLoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanPaymentRequestDto;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class MakeLoanPaymentEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "user";
  public static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";
  public static final String LOAN_ID = "1";

  private final ActiveLoanPaymentRequestDto loanPaymentRequestDto =
      new ActiveLoanPaymentRequestDto(ACCOUNT_NUMBER, BigDecimal.valueOf(282.08));

  @Test
  @WithUserId(USER_ID)
  void shouldSuccessMakeLoanPayment() throws Exception {

    mockMvc
        .perform(
            post(LOANS_URL + "/" + LOAN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loanPaymentRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is(MessageResponseDto.PAYMENT_SUCCEEDED)));
  }

  @ParameterizedTest
  @CsvSource({",20", "1,20", "PL04234567840000000000000002,0"})
  @WithUserId(USER_ID)
  void whenMakeLoanPaymentInvalidRequest_shouldFail(String accountNumber, BigDecimal amount)
      throws Exception {
    final LoanPaymentRequestDto loanRequestDto =
        new ActiveLoanPaymentRequestDto(accountNumber, amount);

    // when
    mockMvc
        .perform(
            post(LOANS_URL + "/" + LOAN_ID)
                .header("authenticated-user-id", USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loanRequestDto)))
        .andExpect(status().is4xxClientError());
  }
}
