package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardController;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

@EnableSpringDataWebSupport
class CardByIdEndpointTest extends AbstractIntegrationTest {

  private static final int CARD_ID = 4;
  private static final String USER_ID = "user";
  private static final String NON_EXISTENT_USER_ID = "nonExistentUserId";

  @Test
  @WithUserId(USER_ID)
  void whenGetCardById_shouldReturnCorrectDetailedCardInfo() throws Exception {
    // given
    final CardDetailedResponseDto expected = MockData.getValidCardDetailedResponseDto();

    // when/then
    mockMvc
        .perform(
            get(
                UriComponentsBuilder.fromPath(CardController.USER_CARDS_URL + "/" + CARD_ID)
                    .toUriString()))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cardholderName", is("ROBERT S.")))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cvv", is(expected.cvv())))
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "$.withdrawLimit", is(expected.withdrawLimit().doubleValue())));
  }

  @Test
  @WithUserId(USER_ID)
  void whenCardForThisUserNotFound_whenGetCardById_shouldReturnNotFoundError() throws Exception {
    mockMvc
        .perform(
            get(
                UriComponentsBuilder.fromPath(CardController.USER_CARDS_URL + "/200")
                    .toUriString()))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
        .andExpect(
            result ->
                assertEquals(
                    "Cannot find card entity with id 200",
                    result.getResolvedException().getMessage()));
  }

  @Test
  @WithUserId(NON_EXISTENT_USER_ID)
  void whenUserWantsToGetNotHisCard_shouldReturnNotFoundError() throws Exception {
    mockMvc
        .perform(
            get(UriComponentsBuilder.fromPath(CardController.USER_CARDS_URL + "/1").toUriString()))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
        .andExpect(
            result ->
                assertEquals(
                    "Cannot find card entity with id 1",
                    result.getResolvedException().getMessage()));
  }
}
