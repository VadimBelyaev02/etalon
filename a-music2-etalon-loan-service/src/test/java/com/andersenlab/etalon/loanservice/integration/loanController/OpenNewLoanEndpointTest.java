package com.andersenlab.etalon.loanservice.integration.loanController;

import static com.andersenlab.etalon.loanservice.controller.LoanController.LOANS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.dto.loan.request.CollectLoanRequestDto;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class OpenNewLoanEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID_WITH_OPEN_LOANS = "user";
  public static final Long LOAN_ORDER_ID = 1L;
  public static final Long REJECTED_LOAN_ORDER_ID = 2L;
  public static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";

  private final CollectLoanRequestDto collectLoanRequestDto =
      new CollectLoanRequestDto(LOAN_ORDER_ID, ACCOUNT_NUMBER);

  @Test
  @WithUserId(USER_ID_WITH_OPEN_LOANS)
  void shouldSuccessOpenNewLoan() throws Exception {

    mockMvc
        .perform(
            post(LOANS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(collectLoanRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is("Loan has been opened successfully")));
  }

  @Test
  @WithUserId(USER_ID_WITH_OPEN_LOANS)
  void shouldFailOpenNewLoan_WhenLOanOrderStatusRejected() throws Exception {

    String expectedErrorMessage =
        String.format("Loan order with ID: %s has been rejected", REJECTED_LOAN_ORDER_ID);

    mockMvc
        .perform(
            post(LOANS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new CollectLoanRequestDto(REJECTED_LOAN_ORDER_ID, ACCOUNT_NUMBER))))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("$.message", is(expectedErrorMessage)));
  }
}
