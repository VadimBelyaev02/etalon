package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardController;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

public class CardByAccountNumbersEndpointTest extends AbstractIntegrationTest {
  private static final String USER_ID = "user";
  private static final Long CARD_ID = 1L;
  private static final String CARD_ID_PARAM = "cardId";

  @Test
  @WithUserId(USER_ID)
  void whenGetActiveCardByAccountNumber_shouldReturnCorrectCardInfo() throws Exception {
    mockMvc
        .perform(
            get(
                UriComponentsBuilder.fromPath(
                        CardController.USER_CARDS_URL + CardController.ACTIVE_CARD)
                    .queryParam("accountNumber", "PL04234567840000000000000001")
                    .toUriString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.status", is("ACTIVE")))
        .andExpect(jsonPath("$.cardholderName", is("ROBERT S.")));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetCardInfoByCardIds_shouldReturnCorrectCardInfo() throws Exception {
    final ShortCardInfoDto expected = MockData.getValidShortCardInfoDto();

    mockMvc
        .perform(
            get(
                UriComponentsBuilder.fromPath(
                        CardController.USER_CARDS_URL + CardController.CARDS_INFO_URI)
                    .queryParam(CARD_ID_PARAM, CARD_ID)
                    .toUriString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].cardProductName", is(expected.cardProductName())))
        .andExpect(jsonPath("$[0].maskedCardNumber", is(expected.maskedCardNumber())))
        .andExpect(jsonPath("$[0].id", is(expected.id().intValue())));
  }
}
