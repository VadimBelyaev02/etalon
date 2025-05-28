package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.controller.CardProductController;
import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GetAllCardProductsTest extends AbstractIntegrationTest {
  @Test
  void whenGetAllCardProducts_shouldSuccess() throws Exception {

    // given
    List<CardProductResponseDto> cardProductEntities =
        List.of(MockData.getValidCardProductResponseDto());

    // when/then
    mockMvc
        .perform(get(CardProductController.CARD_PRODUCTS_URL))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[3].id", is(Math.toIntExact(cardProductEntities.get(0).id()))))
        .andExpect(
            jsonPath("$[3].productType", is(cardProductEntities.get(0).productType().toString())))
        .andExpect(jsonPath("$[3].name", is(cardProductEntities.get(0).name())))
        .andExpect(jsonPath("$[3].issuer", is(cardProductEntities.get(0).issuer().toString())))
        .andExpect(
            jsonPath(
                "$[3].availableCurrencies",
                is(
                    cardProductEntities.get(0).availableCurrencies().stream()
                        .map(Enum::name)
                        .toList())))
        .andExpect(jsonPath("$[3].validity", is(cardProductEntities.get(0).validity())))
        .andExpect(
            jsonPath("$[3].issuanceFee", is(cardProductEntities.get(0).issuanceFee().intValue())))
        .andExpect(
            jsonPath(
                "$[3].maintenanceFee", is(cardProductEntities.get(0).maintenanceFee().intValue())))
        .andExpect(jsonPath("$[3].apr", is(cardProductEntities.get(0).apr())))
        .andExpect(jsonPath("$[3].cashback", is(cardProductEntities.get(0).cashback().intValue())));
  }
}
