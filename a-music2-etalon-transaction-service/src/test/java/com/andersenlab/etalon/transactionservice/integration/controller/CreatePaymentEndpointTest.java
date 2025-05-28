package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidPaymentRequestDto;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.PaymentController;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class CreatePaymentEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "1";

  @Test
  @WithUserId
  void shouldReturnConfirmation() throws Exception {
    PaymentRequestDto paymentRequestDto = getValidPaymentRequestDto();
    mockMvc
        .perform(
            post(PaymentController.PAYMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(paymentRequestDto)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.confirmationId", is(1)));
  }

  @Test
  @WithUserId("2")
  void shouldFailBecauseAccountDoesNotBelongToUser() throws Exception {
    PaymentRequestDto paymentRequestDto = getValidPaymentRequestDto();

    mockMvc
        .perform(
            post(PaymentController.PAYMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(paymentRequestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
        .andExpect(
            result ->
                assertEquals(
                    BusinessException.OPERATION_REJECTED_DUE_TO_SECURITY,
                    Objects.requireNonNull(result.getResolvedException()).getMessage()));
  }

  @Test
  @WithUserId("4")
  void shouldFailBecauseAccountIsBlocked() throws Exception {
    PaymentRequestDto paymentRequestDto =
        getValidPaymentRequestDto().toBuilder()
            .accountNumberWithdrawn("PL48234567840000000000000041")
            .build();

    mockMvc
        .perform(
            post(PaymentController.PAYMENTS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(paymentRequestDto)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result -> assertInstanceOf(BusinessException.class, result.getResolvedException()))
        .andExpect(
            result ->
                assertEquals(
                    BusinessException.ACCOUNT_IS_BLOCKED,
                    Objects.requireNonNull(result.getResolvedException()).getMessage()));
  }
}
