package com.andersenlab.etalon.depositservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.depositservice.MockData;
import com.andersenlab.etalon.depositservice.controller.DepositProductController;
import com.andersenlab.etalon.depositservice.entity.DepositProductEntity;
import com.andersenlab.etalon.depositservice.integration.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

@Sql(
    scripts = {
      "classpath:data/deposit-products-initial-data.sql",
      "classpath:data/deposit-initial-data.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GetAllDepositProductsEndpointTest extends AbstractIntegrationTest {

  @Test
  void shouldSuccessGetAllDepositProducts() throws Exception {

    // given
    List<DepositProductEntity> depositProductEntities =
        List.of(MockData.getValidDepositProductEntity());

    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(DepositProductController.PRODUCTS_URL).toUriString()))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[1].id", is(Math.toIntExact(depositProductEntities.get(0).getId()))))
        .andExpect(jsonPath("$[1].name", is(depositProductEntities.get(0).getName())))
        .andExpect(
            jsonPath(
                "$[1].minDepositPeriod",
                is(depositProductEntities.get(0).getMinDepositPeriod().intValue())))
        .andExpect(
            jsonPath(
                "$[1].maxDepositPeriod",
                is(depositProductEntities.get(0).getMaxDepositPeriod().intValue())))
        .andExpect(jsonPath("$[1].term", is(depositProductEntities.get(0).getTerm().toString())))
        .andExpect(
            jsonPath("$[1].currency", is(depositProductEntities.get(0).getCurrency().toString())))
        .andExpect(
            jsonPath(
                "$[1].interestRate",
                is(depositProductEntities.get(0).getInterestRate().intValue())))
        .andExpect(
            jsonPath(
                "$[1].minOpenAmount",
                is(depositProductEntities.get(0).getMinOpenAmount().intValue())))
        .andExpect(
            jsonPath(
                "$[1].maxDepositAmount",
                is(depositProductEntities.get(0).getMaxDepositAmount().intValue())))
        .andExpect(
            jsonPath(
                "$[1].isEarlyWithdrawal",
                is(depositProductEntities.get(0).getIsEarlyWithdrawal())));
  }
}
