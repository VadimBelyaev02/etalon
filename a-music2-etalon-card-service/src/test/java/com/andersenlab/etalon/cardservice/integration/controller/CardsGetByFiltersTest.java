package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardController;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import com.andersenlab.etalon.cardservice.util.enums.Issuer;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CardsGetByFiltersTest extends AbstractIntegrationTest {
  public static final String USER_ID = "user";
  private List<CardResponseDto> expectedListOfCards;
  private MultiValueMap<String, String> params;

  @BeforeEach
  void setUp() {
    expectedListOfCards = List.of(MockData.getValidCardResponseDto());
    params = new LinkedMultiValueMap<>();
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetAllUserCardsByFilters_shouldSuccess() throws Exception {

    params.add("issuer", Issuer.VISA.name());
    params.add("status", CardStatus.ACTIVE.name());

    mockMvc
        .perform(get(CardController.USER_CARDS_URL).queryParams(params))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is((Math.toIntExact(expectedListOfCards.get(0).id())))))
        .andExpect(
            jsonPath(
                "$[0].expirationDate", is(expectedListOfCards.get(0).expirationDate().toString())))
        .andExpect(jsonPath("$[0].issuer", is(expectedListOfCards.get(0).issuer().toString())))
        .andExpect(jsonPath("$[0].number", is("4246700000000019")))
        .andExpect(jsonPath("$[0].balance", is(expectedListOfCards.get(0).balance().doubleValue())))
        .andExpect(jsonPath("$[0].currency", is(expectedListOfCards.get(0).currency().toString())))
        .andExpect(jsonPath("$[0].isBlocked", is(expectedListOfCards.get(0).isBlocked())))
        .andExpect(jsonPath("$[0].status", is(expectedListOfCards.get(0).status().toString())));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetEmptyListIfNoUserCardsByGivenFilters_shouldSuccess() throws Exception {
    params.add("issuer", Issuer.MASTERCARD.name());
    params.add("status", CardStatus.EXPIRED.name());

    mockMvc
        .perform(get(CardController.USER_CARDS_URL).queryParams(params))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetAllUserCardsByGivenEmptyFilters_shouldSuccess() throws Exception {

    mockMvc
        .perform(get(CardController.USER_CARDS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(Math.toIntExact(expectedListOfCards.get(0).id()))))
        .andExpect(
            jsonPath(
                "$[0].expirationDate", is(expectedListOfCards.get(0).expirationDate().toString())))
        .andExpect(jsonPath("$[0].issuer", is(expectedListOfCards.get(0).issuer().toString())))
        .andExpect(jsonPath("$[0].number", is("4246700000000019")))
        .andExpect(jsonPath("$[0].balance", is(expectedListOfCards.get(0).balance().doubleValue())))
        .andExpect(jsonPath("$[0].isBlocked", is(expectedListOfCards.get(0).isBlocked())))
        .andExpect(jsonPath("$[0].status", is(expectedListOfCards.get(0).status().toString())));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetAllUserCardsByIssuerFilter_shouldSuccess() throws Exception {

    params.add("issuer", Issuer.VISA.name());

    mockMvc
        .perform(get(CardController.USER_CARDS_URL).queryParams(params))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is((Math.toIntExact(expectedListOfCards.get(0).id())))))
        .andExpect(
            jsonPath(
                "$[0].expirationDate", is(expectedListOfCards.get(0).expirationDate().toString())))
        .andExpect(jsonPath("$[0].issuer", is(expectedListOfCards.get(0).issuer().toString())))
        .andExpect(jsonPath("$[0].number", is("4246700000000019")))
        .andExpect(jsonPath("$[0].balance", is(expectedListOfCards.get(0).balance().doubleValue())))
        .andExpect(jsonPath("$[0].isBlocked", is(expectedListOfCards.get(0).isBlocked())))
        .andExpect(jsonPath("$[0].status", is(expectedListOfCards.get(0).status().toString())));
  }

  @Test
  @WithUserId(USER_ID)
  void whenGetAllUserCards_shouldReturnCardListWithActiveCardsAtTop() throws Exception {

    mockMvc
        .perform(get(CardController.USER_CARDS_URL))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[1].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[2].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[3].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[4].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[5].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[6].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[7].status", is(CardStatus.ACTIVE.toString())))
        .andExpect(jsonPath("$[8].status", is(CardStatus.BLOCKED.toString())))
        .andExpect(jsonPath("$[9].status", is(CardStatus.BLOCKED.toString())));
  }
}
