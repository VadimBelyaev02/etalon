package com.andersenlab.etalon.loanservice.integration.loanOrderController;

import static com.andersenlab.etalon.loanservice.controller.LoanOrderController.LOAN_ORDERS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class CreateNewLoanOrderEndpointTest extends AbstractIntegrationTest {

  private LoanOrderRequestDto loanOrderRequestDto;

  @BeforeEach
  void setUp() {
    loanOrderRequestDto = MockData.getValidLoanOrderRequestDto();
  }

  @Test
  @WithUserId
  void shouldSuccessCreateNewLoanOrder() throws Exception {
    mockMvc
        .perform(
            post(LOAN_ORDERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loanOrderRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(
            jsonPath("$.message", is("Your request to open a loan has been sent to review")));
  }
}
