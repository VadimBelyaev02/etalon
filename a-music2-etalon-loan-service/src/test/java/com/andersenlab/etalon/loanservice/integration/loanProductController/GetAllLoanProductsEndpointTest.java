package com.andersenlab.etalon.loanservice.integration.loanProductController;

import static com.andersenlab.etalon.loanservice.controller.LoanProductController.PRODUCTS_URL;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.loanservice.MockData;
import com.andersenlab.etalon.loanservice.entity.LoanProductEntity;
import com.andersenlab.etalon.loanservice.integration.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetAllLoanProductsEndpointTest extends AbstractIntegrationTest {

  @Test
  void shouldSuccessGetAllLoanProducts() throws Exception {

    // given
    List<LoanProductEntity> loanProductEntities = List.of(MockData.getValidLoanProductEntity());

    mockMvc
        .perform(get(PRODUCTS_URL))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[1].id", is(Math.toIntExact(loanProductEntities.get(0).getId()))))
        .andExpect(jsonPath("$[1].name", is(loanProductEntities.get(0).getName())))
        .andExpect(jsonPath("$[1].duration", is(loanProductEntities.get(0).getDuration())))
        .andExpect(jsonPath("$[1].apr", is(loanProductEntities.get(0).getApr().doubleValue())))
        .andExpect(
            jsonPath(
                "$[1].requiredGuarantors", is(loanProductEntities.get(0).getRequiredGuarantors())))
        .andExpect(
            jsonPath("$[1].minAmount", is(loanProductEntities.get(0).getMinAmount().intValue())))
        .andExpect(
            jsonPath("$[1].maxAmount", is(loanProductEntities.get(0).getMaxAmount().intValue())))
        .andExpect(
            jsonPath(
                "$[1].monthlyCommission",
                is(loanProductEntities.get(0).getMonthlyCommission().intValue())));
  }
}
