package com.andersenlab.etalon.transactionservice.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.PaymentController;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

public class ProcessPaymentEndpointTest extends AbstractIntegrationTest {
  public static final String USER_ID = "user";
  public static final Long INVALID_PAYMENT_ID = 101L;

  @Test
  @WithUserId(USER_ID)
  void shouldFail_WhenInvalidPaymentId() throws Exception {
    mockMvc
        .perform(
            post(UriComponentsBuilder.fromPath(
                        PaymentController.PAYMENTS_URL
                            + "/"
                            + INVALID_PAYMENT_ID
                            + PaymentController.PAYMENT_CONFIRM_URI)
                    .toUriString())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
  }
}
