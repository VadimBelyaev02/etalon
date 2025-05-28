package com.andersenlab.etalon.accountservice.integration.controller;

import static com.andersenlab.etalon.accountservice.controller.internal.InternalAccountController.ACCOUNT_V1_WITHDRAWN_URL;
import static com.andersenlab.etalon.accountservice.controller.internal.InternalAccountController.URI;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.dto.account.request.AccountWithdrawByAccountNumberRequestDto;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class WithdrawAccountBalanceByAccountNumberEndpointTest extends AbstractIntegrationTest {

  private static final String ACCOUNT_NUMBER = "PL04234567840000000000000003";

  @Test
  void whenWithdrawAccountBalance_thenSuccess() throws Exception {
    // given
    final AccountWithdrawByAccountNumberRequestDto accountWithdrawByIdRequestDto =
        new AccountWithdrawByAccountNumberRequestDto(BigDecimal.valueOf(1000.00));

    // when/then
    mockMvc
        .perform(
            post(URI + ACCOUNT_V1_WITHDRAWN_URL, ACCOUNT_NUMBER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(accountWithdrawByIdRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("Account balance withdrawn successfully")));
  }
}
