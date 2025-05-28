package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.controller.AccountController;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.accountservice.util.enums.Currency;
import org.junit.jupiter.api.Test;

public class GetAllAccountInfoBySelectedOptionEndpointTest extends AbstractIntegrationTest {
  public static final String ACCOUNT_NUMBER = "PL04234567840000000000000001";
  public static final String PHONE_NUMBER = "48529304229";
  public static final String CARD_NUMBER = "4246700000000100";
  public static final Long ACCOUNT_ID = 2L;

  @Test
  void whenGetAccountInfoByValidAccountNumber_thenSuccess() throws Exception {
    mockMvc
        .perform(get(AccountController.ACCOUNT_V2_URL).param("accountNumber", ACCOUNT_NUMBER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName", is("Pavel K.")))
        .andExpect(jsonPath("$.accounts[0].id", is(ACCOUNT_ID.intValue())))
        .andExpect(jsonPath("$.accounts[0].accountNumber", is(ACCOUNT_NUMBER)))
        .andExpect(jsonPath("$.accounts[0].currency", is(Currency.PLN.name())));
  }

  @Test
  void whenGetAccountInfoByValidPhoneNumber_thenSuccess() throws Exception {
    mockMvc
        .perform(get(AccountController.ACCOUNT_V2_URL).param("phoneNumber", PHONE_NUMBER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName", is("Pavel K.")))
        .andExpect(jsonPath("$.accounts[0].id", is(ACCOUNT_ID.intValue())))
        .andExpect(jsonPath("$.accounts[0].accountNumber", is(ACCOUNT_NUMBER)))
        .andExpect(jsonPath("$.accounts[0].currency", is(Currency.PLN.name())));
  }

  @Test
  void whenGetAccountInfoByValidCardNumber_thenSuccess() throws Exception {
    mockMvc
        .perform(get(AccountController.ACCOUNT_V2_URL).param("cardNumber", CARD_NUMBER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullName", is("Pavel K.")))
        .andExpect(jsonPath("$.accounts[0].id", is(ACCOUNT_ID.intValue())))
        .andExpect(jsonPath("$.accounts[0].accountNumber", is(ACCOUNT_NUMBER)))
        .andExpect(jsonPath("$.accounts[0].currency", is(Currency.PLN.name())));
  }

  @Test
  void whenGetAccountInfoByInvalidAccountNumber_shouldFail() throws Exception {
    mockMvc
        .perform(get(AccountController.ACCOUNT_V2_URL).param("accountNumber", ACCOUNT_NUMBER + "1"))
        .andExpect(status().isBadRequest());
  }
}
