package com.andersenlab.etalon.depositservice.controller;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

public interface DepositOrderController {
  String API_V1_URI = "/api/v1";
  String DEPOSIT_ORDER_URI = "/deposit-orders";
  String DEPOSIT_ORDER_ID_URI = "/{depositOrderId}";
  String DEPOSIT_ORDER_CONFIRM = "/confirm";
  String DEPOSIT_ORDER_URL = API_V1_URI + DEPOSIT_ORDER_URI;
  String DEPOSIT_ORDER_ID_CONFIRM_URL = DEPOSIT_ORDER_ID_URI + DEPOSIT_ORDER_CONFIRM;

  @Operation(summary = "Process opening new deposit")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find deposit order with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto processOpeningNewDeposit(@PathVariable final Long depositOrderId);
}
