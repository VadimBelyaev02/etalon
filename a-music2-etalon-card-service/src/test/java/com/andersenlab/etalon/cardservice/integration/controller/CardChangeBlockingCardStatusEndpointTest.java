package com.andersenlab.etalon.cardservice.integration.controller;

import static com.andersenlab.etalon.cardservice.exception.BusinessException.ACCOUNT_IS_BLOCKED;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.NOT_FOUND_CARD_BY_ID;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardBlockingController;
import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.entity.CardEntity;
import com.andersenlab.etalon.cardservice.exception.BusinessException;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.cardservice.util.enums.CardBlockingReason;
import com.andersenlab.etalon.cardservice.util.enums.CardStatus;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

class CardChangeBlockingCardStatusEndpointTest extends AbstractIntegrationTest {

  public static final String USER_ID = "user";
  public static final String CARD_ID = "1";
  public static final String CARD_ID_WITH_LINKED_DEPOSITS = "10L";
  private ChangeCardStatusRequestDto request;

  @BeforeEach
  void setUp() {
    request = MockData.getValidBlockChangeCardStatusRequestDto();
  }

  @Test
  @WithUserId(USER_ID)
  void whenBlockAndReissue_thenNewCardCreatedWithSameAccount() throws Exception {
    ChangeCardStatusRequestDto changeRequest = request.toBuilder().id(12L).build();
    String expectedLinkedAccount =
        cardRepository.findById(changeRequest.id()).get().getAccountNumber();
    // when/then
    mockMvc
        .perform(
            put(CardBlockingController.BLOCK_CARD_URL, CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(changeRequest)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.accountNumber", is(expectedLinkedAccount)));
    assertEquals(CardStatus.BLOCKED, cardRepository.findById(changeRequest.id()).get().getStatus());
  }

  @Test
  @WithUserId(USER_ID)
  void whenRequestCardBlockAndReissueWithWrongBlockRequest_thenReturnBadRequestAndMessage()
      throws Exception {
    // given
    final ChangeCardStatusRequestDto requestUnlock =
        request.toBuilder().id(1L).blockingReason(null).build();

    // when/then
    mockMvc
        .perform(
            put(CardBlockingController.BLOCK_CARD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(requestUnlock)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            jsonPath(
                "$.message", is(BusinessException.NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST)));
  }

  @Test
  @WithUserId(USER_ID)
  @Sql(
      scripts = "file:src/test/resources/data/insert-card-with-linked-deposit.sql",
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void whenRequestCardBlockWithLinkedDeposits_thenReturnConflictAndMessage() throws Exception {
    // given
    final ChangeCardStatusRequestDto blockCardRequestWithLinkedDeposits =
        request.toBuilder().id(10L).blockingReason(CardBlockingReason.DAMAGED.name()).build();

    // when/then
    mockMvc
        .perform(
            put(CardBlockingController.BLOCK_CARD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(blockCardRequestWithLinkedDeposits)))
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.CONFLICT.value()))
        .andExpect(jsonPath("$.message", is(BusinessException.CARD_ACCOUNT_LINKED_TO_DEPOSIT)));

    CardEntity card = cardRepository.findById(10L).orElseThrow();
    Assertions.assertFalse(card.getIsBlocked());
    Assertions.assertNull(card.getBlockingReason());
  }

  @Test
  @WithUserId(USER_ID)
  void whenGivenBlockedAccountNumberAndReissueCard_thenReturnBadRequestAndMessage()
      throws Exception {
    // given
    ChangeCardStatusRequestDto changeRequest = request.toBuilder().id(13L).build();

    // when/then
    mockMvc
        .perform(
            put(CardBlockingController.BLOCK_CARD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(changeRequest)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(
            result ->
                assertEquals(
                    ACCOUNT_IS_BLOCKED,
                    Objects.requireNonNull(result.getResolvedException()).getMessage()));
  }

  @Test
  @WithUserId(USER_ID)
  void whenRequestCardBlockAndReissueWithWrongCardId_thenReturnBadRequestAndMessage()
      throws Exception {
    // given
    long wrongCardId = 14L;
    final ChangeCardStatusRequestDto changeRequest = request.toBuilder().id(wrongCardId).build();

    // when/then
    mockMvc
        .perform(
            put(CardBlockingController.BLOCK_CARD_URL, CARD_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(changeRequest)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("$.message", is(NOT_FOUND_CARD_BY_ID.formatted(wrongCardId))));
  }
}
