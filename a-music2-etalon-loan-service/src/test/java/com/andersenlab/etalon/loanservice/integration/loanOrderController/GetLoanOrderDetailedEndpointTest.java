package com.andersenlab.etalon.loanservice.integration.loanOrderController;

import static com.andersenlab.etalon.loanservice.controller.LoanOrderController.LOAN_ORDERS_URL;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.exception.BusinessException;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class GetLoanOrderDetailedEndpointTest extends AbstractIntegrationTest {
  private static final String VALID_ORDER_ID_PATH = "1";
  private static final String INVALID_ORDER_ID_PATH = "200";
  private static final String PARAM_STATUS = "status";

  @Test
  @WithUserId("user")
  void whenGetLoanOrderDetailed_thenSuccess() throws Exception {
    // given
    final LoanOrderDetailedResponseDto response = MockData.getValidLoanOrderDetailedResponseDto();

    mockMvc
        .perform(get(LOAN_ORDERS_URL + "/" + VALID_ORDER_ID_PATH))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(Math.toIntExact(response.id()))))
        .andExpect(jsonPath("$.borrower", is(response.borrower())))
        .andExpect(jsonPath("$.productName", is(response.productName())))
        .andExpect(jsonPath("$.duration", is(response.duration())))
        .andExpect(jsonPath("$.amount", is((response.amount()).doubleValue())))
        .andExpect(jsonPath("$.apr", is(response.apr().doubleValue())))
        .andExpect(jsonPath("$.status", is(response.status().toString())))
        .andExpect(jsonPath("$.guarantors", anything()));
  }

  @Test
  void whenGetLoanOrderDetailed_thenThrowBusinessException() throws Exception {
    final String response =
        String.format(BusinessException.LOAN_ORDER_NOT_FOUND_BY_ID, INVALID_ORDER_ID_PATH);

    mockMvc
        .perform(
            get(LOAN_ORDERS_URL + "/" + INVALID_ORDER_ID_PATH)
                .header("authenticated-user-id", "user"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.message", is(response)));
  }
}
