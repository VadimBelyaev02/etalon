package com.andersenlab.etalon.cardservice.controller;

import static com.andersenlab.etalon.cardservice.exception.BusinessException.ACCOUNT_IS_BLOCKED;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.ACCOUNT_LINKED_TO_ANOTHER_USER;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.CARD_ACCOUNT_LINKED_TO_DEPOSIT;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.CARD_IS_ALREADY_BLOCKED;
import static com.andersenlab.etalon.cardservice.exception.BusinessException.NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST;

import com.andersenlab.etalon.cardservice.dto.card.request.ChangeCardStatusRequestDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardBlockingReasonResponseDto;
import com.andersenlab.etalon.cardservice.dto.card.response.CardDetailedResponseDto;
import com.andersenlab.etalon.cardservice.dto.common.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;

public interface CardBlockingController {

  String API_V1_URI = "/api/v1";
  String BLOCKING_URI = "/blocking";
  String CARDS_URI = "/cards";
  String REASON_BLOCKING_URI = "/reasons";

  String BLOCK_CARD_URL = API_V1_URI + BLOCKING_URI + CARDS_URI;
  String CARD_BLOCKING_REASONS_URL = API_V1_URI + BLOCKING_URI + CARDS_URI + REASON_BLOCKING_URI;

  @Operation(summary = "Get reasons for card blocking")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema =
                                @Schema(implementation = CardBlockingReasonResponseDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find card entity",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  List<CardBlockingReasonResponseDto> getReasonsCardBlocking();

  @Operation(summary = "Block and Reissue card")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "message=Card is blocked and reissued",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CardDetailedResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = NOT_COMPLETED_USER_CARD_CHANGE_STATUS_REQUEST,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "403",
            description = ACCOUNT_LINKED_TO_ANOTHER_USER,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find card entity",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = ACCOUNT_IS_BLOCKED,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = CARD_IS_ALREADY_BLOCKED,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = CARD_ACCOUNT_LINKED_TO_DEPOSIT,
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  CardDetailedResponseDto blockCardAndReissue(@Valid ChangeCardStatusRequestDto dto);
}
