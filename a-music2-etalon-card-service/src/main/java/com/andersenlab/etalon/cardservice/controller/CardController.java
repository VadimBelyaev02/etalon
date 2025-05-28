package com.andersenlab.etalon.cardservice.controller;

import com.andersenlab.etalon.cardservice.dto.card.request.CardCreationRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.CardDetailsRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.request.RequestFilterDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.ShortCardInfoDto;
import com.andersenlab.etalon.cardservice.dto.common.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

public interface CardController {

  String API_V1_URI = "/api/v1";
  String CARDS_URI = "/cards";
  String ID_PATH = "/{id}";
  String CARD_ID_PATH = "/{cardId}";
  String CARDS_INFO_URI = "/cards-info";
  String ACTIVE_CARD = "/active";
  String USER_CARDS_URL = API_V1_URI + CARDS_URI;
  String USER_CARDS_BY_ID_URL = API_V1_URI + CARDS_URI + ID_PATH;

  @Operation(summary = "Get short info about card by account number")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card info provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = ShortCardInfoDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find card entity",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  List<ShortCardInfoDto> getShortCardInfoByCardIds(List<Long> cardId);

  @Operation(summary = "Update card details")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card details are changed",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CardDetailsRequestDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find card entity",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto updateCardDetails(
      Long id, @Valid final CardDetailsRequestDto cardDetailsRequestDto);

  @Operation(summary = "Get all user cards")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "All cards provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class))))
      })
  List<CardResponseDto> getAllUserCards(@ParameterObject RequestFilterDto filters);

  @Operation(summary = "Get user card by id")
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
  CardDetailedResponseDto getUserCardById(@PathVariable long cardId);

  @Operation(summary = "Open new card")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Card created successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CardDetailedResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Card product was not found with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  CardDetailedResponseDto openUserCard(@Valid CardCreationRequestDto cardCreation);

  @Operation(summary = "Get active card for user")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "The information about active card was fetched",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = CardResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  CardResponseDto getActiveUserCard(String accountNumber);
}
