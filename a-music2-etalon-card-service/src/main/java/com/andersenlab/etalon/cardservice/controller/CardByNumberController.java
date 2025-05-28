package com.andersenlab.etalon.cardservice.controller;

import static com.andersenlab.etalon.cardservice.util.Constants.CARD_NUMBER_PATTERN;
import static com.andersenlab.etalon.cardservice.util.Constants.WRONG_CARD_NUMBER_MESSAGE;

import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.common.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

public interface CardByNumberController {
  String API_V1_URI = "/api/v1";
  String CARDS_BY_NUMBER_URI = "/cards-by-number";
  String CARD_NUMBER_PATH = "/{cardNumber}";
  String USER_CARD_BY_NUMBER_URL = API_V1_URI + CARDS_BY_NUMBER_URI + CARD_NUMBER_PATH;

  @Operation(summary = "Get user card by  number")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card info provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CardDetailedResponseDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find card entity",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  CardDetailedResponseDto getUserCardByNumber(
      @PathVariable @Pattern(regexp = CARD_NUMBER_PATTERN, message = WRONG_CARD_NUMBER_MESSAGE)
          String cardNumber);
}
