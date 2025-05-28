package com.andersenlab.etalon.accountservice.integration.controller;

import static com.andersenlab.etalon.accountservice.controller.internal.InternalAccountController.ACCOUNT_V1_REPLENISHED_URL;
import static com.andersenlab.etalon.accountservice.controller.internal.InternalAccountController.URI;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.dto.account.request.AccountReplenishByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ReplenishAccountBalanceByAccountNumberEndpointTest extends AbstractIntegrationTest {
  private static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";

  @Test
  void whenReplenishAccountBalance_thenSuccess() throws Exception {
    // given
    final AccountReplenishByAccountNumberRequestDto accountReplenishByIdRequestDto =
        new AccountReplenishByAccountNumberRequestDto(BigDecimal.valueOf(1000.00));

    // when/then
    mockMvc
        .perform(
            post(URI + ACCOUNT_V1_REPLENISHED_URL, ACCOUNT_NUMBER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(accountReplenishByIdRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Account balance replenished successfully")));
  }
}
