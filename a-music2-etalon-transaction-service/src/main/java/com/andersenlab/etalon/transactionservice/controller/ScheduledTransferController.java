package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CustomPageDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.ScheduledTransferDto;
import com.andersenlab.etalon.transactionservice.util.filter.ScheduledTransferFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

public interface ScheduledTransferController {
  String API_V1_URI = "/api/v1";
  String SCHEDULED_TRANSFER_URI = "/scheduled-transfers";
  String SCHEDULED_TRANSFER_URL = API_V1_URI + SCHEDULED_TRANSFER_URI;

  @Operation(summary = "Get all scheduled transfers by user id — US 10.0")
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
                            schema = @Schema(implementation = ScheduledTransferDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  CustomPageDto<ScheduledTransferDto> getScheduledTransferByUserId(
      @ParameterObject ScheduledTransferFilter scheduledTransferFilter, Pageable pageable);
}
