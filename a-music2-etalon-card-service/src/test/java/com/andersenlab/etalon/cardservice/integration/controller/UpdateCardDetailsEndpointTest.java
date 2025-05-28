package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardController;
import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class UpdateCardDetailsEndpointTest extends AbstractIntegrationTest {

  public static final String CARD_ID = "1";
  public static final String BLOCKED_CARD_ID = "11";
  public static final String USER_ID = "user";
  public static final String NON_EXISTING_CARD_ID = "999";
  private CardDetailsRequestDto cardDetailsRequestDto;

  @BeforeEach
  public void setUp() {
    cardDetailsRequestDto = MockData.getValidCardDetailsRequestDto();
  }

  @Test
  @WithUserId(USER_ID)
  void whenUpdateCardDetails_thenReturnOkAndMessage() throws Exception {

    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cardDetailsRequestDto)))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$.message", is("Card details changed successfully")));
  }

  @Test
  void whenUpdateCardDetails_thenReturnNotFoundAndMessage() throws Exception {

    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, NON_EXISTING_CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cardDetailsRequestDto)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(
            jsonPath(
                "$.message",
                is(String.format(BusinessException.NOT_FOUND_CARD_BY_ID, NON_EXISTING_CARD_ID))));
  }

  @WithUserId(USER_ID)
  @ParameterizedTest
  @CsvSource(
      value = {
        "0,10,10",
        "200001,10,10",
        "10,0,10",
        "10,100001,10",
        "10,10,0",
        "10,10,100001",
        "NULL,NULL,NULL"
      },
      nullValues = "NULL")
  void whenUpdateCardDetailsWithInvalidRequest_shouldFail(
      BigDecimal withdrawLimit, BigDecimal transferLimit, BigDecimal dailyExpenseLimit)
      throws Exception {
    // given
    CardDetailsRequestDto requestDto =
        CardDetailsRequestDto.builder()
            .withdrawLimit(withdrawLimit)
            .transferLimit(transferLimit)
            .dailyExpenseLimit(dailyExpenseLimit)
            .build();

    // when
    // then
    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().is4xxClientError());
  }

  @WithUserId(USER_ID)
  @Test
  void whenUpdateCardDetails_shouldSuccess() throws Exception {
    // given
    final String expected = "Card details changed successfully";
    // when
    // then
    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(cardDetailsRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(expected)));
  }

  @WithUserId(USER_ID)
  @ParameterizedTest
  @CsvSource(value = {"200000, 100000, 100000"})
  void whenUpdateBlockedCardDetails_shouldFail(
      BigDecimal withdrawLimit, BigDecimal transferLimit, BigDecimal dailyExpenseLimit)
      throws Exception {
    // given
    CardDetailsRequestDto requestDto =
        CardDetailsRequestDto.builder()
            .withdrawLimit(withdrawLimit)
            .transferLimit(transferLimit)
            .dailyExpenseLimit(dailyExpenseLimit)
            .build();

    // then
    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, BLOCKED_CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().isBadRequest());
  }

  @WithUserId(USER_ID)
  @ParameterizedTest
  @CsvSource(value = {"200001, 100000, 100000", "200000, 100001, 100000", "200000, 100000, 100001"})
  void whenUpdatePolishCardWithInvalidLimits_shouldFail(
      BigDecimal withdrawLimit, BigDecimal transferLimit, BigDecimal dailyExpenseLimit)
      throws Exception {
    // given
    CardDetailsRequestDto requestDto =
        CardDetailsRequestDto.builder()
            .withdrawLimit(withdrawLimit)
            .transferLimit(transferLimit)
            .dailyExpenseLimit(dailyExpenseLimit)
            .build();

    // then
    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().isBadRequest());
  }

  @WithUserId(USER_ID)
  @ParameterizedTest
  @CsvSource(value = {"10001, 5000, 5000", "10000, 5001, 5000", "10000, 5000, 5001"})
  void whenUpdateEurOrUsdCardWithInvalidLimits_shouldFail(
      BigDecimal withdrawLimit, BigDecimal transferLimit, BigDecimal dailyExpenseLimit)
      throws Exception {
    // given
    CardDetailsRequestDto requestDto =
        CardDetailsRequestDto.builder()
            .withdrawLimit(withdrawLimit)
            .transferLimit(transferLimit)
            .dailyExpenseLimit(dailyExpenseLimit)
            .build();

    // then
    mockMvc
        .perform(
            patch(CardController.USER_CARDS_BY_ID_URL, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestDto)))
        .andExpect(status().isBadRequest());
  }
}
