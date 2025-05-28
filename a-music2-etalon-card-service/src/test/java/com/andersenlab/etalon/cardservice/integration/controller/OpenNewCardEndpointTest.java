package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.annotation.WithUserId;
import com.andersenlab.etalon.cardservice.controller.CardController;
import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import com.andersenlab.etalon.cardservice.service.CardProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class OpenNewCardEndpointTest extends AbstractIntegrationTest {
  public static final String USER_ID = "user";

  @Autowired CardProductService cardProductService;
  private ChangeCardStatusRequestDto changeCardStatusRequestDto;

  @BeforeEach
  void setUp() {

    changeCardStatusRequestDto = MockData.getValidBlockChangeCardStatusRequestDto();
  }

  @Test
  @WithUserId(USER_ID)
  void whenOpenNewCard_thenReturnOkAndCardDetailedResponse() throws Exception {
    // given
    final CardCreationRequestDto request = MockData.getValidCardCreationRequestDto();
    final CardDetailedResponseDto response =
        MockData.getValidCardDetailedResponseDto().toBuilder().id(50L).build();

    // when/then
    mockMvc
        .perform(
            post(CardController.USER_CARDS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.CREATED.value()))
        .andExpect(jsonPath("$.isBlocked", is(response.isBlocked())))
        .andExpect(jsonPath("$.status", is(response.status().toString())));
  }
}
