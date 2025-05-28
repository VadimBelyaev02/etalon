package com.andersenlab.etalon.accountservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.accountservice.annotation.WithUserId;
import com.andersenlab.etalon.accountservice.controller.AccountByNumberController;
import com.andersenlab.etalon.accountservice.exception.BusinessExceptionQ;
import com.andersenlab.etalon.accountservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class DeleteAccountEndpointTest extends AbstractIntegrationTest {

  public static final String ACCOUNT_WITH_ZERO_BALANCE = "PL04234567840000000000000002";
  public static final String ACCOUNT_WITH_POSITIVE_BALANCE = "PL04234567840000000000000003";
  public static final String NOT_EXISTING_ACCOUNT_NUMBER = "PL04234567840000111000000010";

  @Test
  @WithUserId
  void whenDeleteAccountWithZeroBalance_thenSuccess() throws Exception {

    mockMvc
        .perform(
            delete(
                AccountByNumberController.ACCOUNT_BY_NUMBER_URL
                    + AccountByNumberController.ACCOUNT_NUMBER_PATH,
                ACCOUNT_WITH_ZERO_BALANCE))
        .andExpect(status().isOk());
  }

  @Test
  @WithUserId
  void whenDeleteAccountWithPositiveBalance_thenFail() throws Exception {

    mockMvc
        .perform(
            delete(
                AccountByNumberController.ACCOUNT_BY_NUMBER_URL
                    + AccountByNumberController.ACCOUNT_NUMBER_PATH,
                ACCOUNT_WITH_POSITIVE_BALANCE))
        .andExpect(status().isConflict())
        .andExpect(
            jsonPath("$.message", is(BusinessExceptionQ.ACCOUNT_DELETION_RESTRICTION_MESSAGE)));
  }

  @Test
  void whenDeleteNotExistingAccount_thenFail() throws Exception {

    mockMvc
        .perform(
            delete(
                AccountByNumberController.ACCOUNT_BY_NUMBER_URL
                    + AccountByNumberController.ACCOUNT_NUMBER_PATH,
                NOT_EXISTING_ACCOUNT_NUMBER))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.message",
                is(
                    String.format(
                        BusinessExceptionQ.NOT_FOUND_ACCOUNT_BY_IBAN_AND_USER_ID,
                        NOT_EXISTING_ACCOUNT_NUMBER,
                        null))));
  }
}
