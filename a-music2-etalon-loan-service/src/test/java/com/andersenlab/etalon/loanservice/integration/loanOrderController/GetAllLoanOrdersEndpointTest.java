package com.andersenlab.etalon.loanservice.integration.loanOrderController;

import static com.andersenlab.etalon.loanservice.controller.LoanOrderController.LOAN_ORDERS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.annotation.WithUserId;
import com.andersenlab.etalon.loanservice.entity.LoanOrderEntity;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetAllLoanOrdersEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "user";
  public static final String USER_ID_WITHOUT_ORDERS = "100";

  @Test
  @WithUserId(USER_ID)
  void whenGetAllLoanOrdersByUserId_shouldSuccess() throws Exception {
    // given
    List<LoanOrderEntity> loanOrderEntities = List.of(MockData.getValidLoanOrderEntity());

    mockMvc
        .perform(get(LOAN_ORDERS_URL))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(Math.toIntExact(loanOrderEntities.get(0).getId()))))
        .andExpect(
            jsonPath("$[0].productName", is(loanOrderEntities.get(0).getProduct().getName())))
        .andExpect(
            jsonPath("$[0].duration", is(loanOrderEntities.get(0).getProduct().getDuration())))
        .andExpect(jsonPath("$[0].amount", is(loanOrderEntities.get(0).getAmount().doubleValue())))
        .andExpect(jsonPath("$[0].status", is(loanOrderEntities.get(0).getStatus().toString())));
  }

  @Test
  void whenGetNoLoanOrders_thenReturnEmptyList() throws Exception {

    mockMvc
        .perform(get(LOAN_ORDERS_URL).header("authenticated-user-id", USER_ID_WITHOUT_ORDERS))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$").isEmpty());
  }
}
