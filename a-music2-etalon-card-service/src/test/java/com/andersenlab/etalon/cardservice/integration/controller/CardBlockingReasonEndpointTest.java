package com.andersenlab.etalon.cardservice.integration.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andersenlab.etalon.cardservice.MockData;
import com.andersenlab.etalon.cardservice.controller.CardBlockingController;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.integration.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

class CardBlockingReasonEndpointTest extends AbstractIntegrationTest {

  @Test
  void whenRequestGetCardBlockingReasons_shouldReturnOkAndListCardBlockingReasonResponseDto()
      throws Exception {
    // given
    final List<CardBlockingReasonResponseDto> expected =
        MockData.getValidListCardBlockingReasonResponseDto();

    // when/then
    mockMvc
        .perform(
            get(
                UriComponentsBuilder.fromPath(CardBlockingController.CARD_BLOCKING_REASONS_URL)
                    .toUriString()))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andExpect(jsonPath("$[0].id", is(expected.get(0).id().intValue())))
        .andExpect(jsonPath("$[1].reason", is(expected.get(1).reason())))
        .andExpect(jsonPath("$[2].description", is(expected.get(2).description())));
  }
}
