package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidPaymentTypeDto;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.PaymentTypeController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.exception.BusinessException;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class GetPaymentTypeEndpointTest extends AbstractIntegrationTest {
  public static final String USER_ID = "1";

  @Test
  @WithUserId
  void whenGetPayment_thenSuccess() throws Exception {
    PaymentTypeResponseDto paymentTypeResponseDto = getValidPaymentTypeDto();

    mockMvc
        .perform(
            get(PaymentTypeController.PAYMENT_TYPES_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(paymentTypeResponseDto.getId().intValue())))
        .andExpect(jsonPath("$.name", is(paymentTypeResponseDto.getName())))
        .andExpect(jsonPath("$.type", is(paymentTypeResponseDto.getType())))
        .andExpect(jsonPath("$.iban", is(paymentTypeResponseDto.getIban())))
        .andExpect(jsonPath("$.fee", is(paymentTypeResponseDto.getFee().doubleValue())));
  }

  @Test
  @WithUserId
  void whenGetPaymentWithWrongId_thenFail() throws Exception {

    mockMvc
        .perform(
            get(PaymentTypeController.PAYMENT_TYPES_URL + "/9999")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.message", is(String.format(BusinessException.NO_PAYMENT_TYPE_FOUND, 9999))));
  }
}
