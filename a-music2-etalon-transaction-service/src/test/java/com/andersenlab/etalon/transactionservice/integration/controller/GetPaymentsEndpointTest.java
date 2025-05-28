package com.andersenlab.etalon.transactionservice.integration.controller;

import static com.andersenlab.etalon.transactionservice.MockData.getValidPaymentTypeDto;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.transactionservice.annotation.WithUserId;
import com.andersenlab.etalon.transactionservice.controller.PaymentTypeController;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.transactionservice.util.enums.PaymentType;
import com.andersenlab.etalon.transactionservice.util.enums.TaxType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class GetPaymentsEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "1";

  @Test
  @WithUserId
  void whenGetPayments_thenSuccess() throws Exception {
    PaymentTypeResponseDto paymentTypeResponseDto = getValidPaymentTypeDto();

    mockMvc
        .perform(
            get(PaymentTypeController.PAYMENT_TYPES_URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(paymentTypeResponseDto.getId().intValue())))
        .andExpect(jsonPath("$[0].name", is(paymentTypeResponseDto.getName())))
        .andExpect(jsonPath("$[0].type", is(paymentTypeResponseDto.getType())))
        .andExpect(jsonPath("$[0].iban", is(paymentTypeResponseDto.getIban())))
        .andExpect(jsonPath("$[0].fee", is(paymentTypeResponseDto.getFee().doubleValue())));
  }

  @Test
  @WithUserId
  void whenGetPaymentsWithTaxesFilter_thenSuccess() throws Exception {

    mockMvc
        .perform(
            get(PaymentTypeController.PAYMENT_TYPES_URL)
                .param("type", "TAXES")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(11)))
        .andExpect(jsonPath("$[0].name", is("Warsaw department Property tax")))
        .andExpect(jsonPath("$[0].type", is(String.valueOf(PaymentType.TAXES_AND_FINES))))
        .andExpect(jsonPath("$[0].iban", is("PL56101000712223140244000000")))
        .andExpect(jsonPath("$[0].fee", is(0.3)))
        .andExpect(jsonPath("$[0].taxType", is(String.valueOf(TaxType.PROPERTY_TAX))))
        .andExpect(jsonPath("$[0].recipientName", is("Taxes PL")))
        .andExpect(jsonPath("$[0].taxDepartmentName", is("Warsaw Tax Office")));
  }
}
