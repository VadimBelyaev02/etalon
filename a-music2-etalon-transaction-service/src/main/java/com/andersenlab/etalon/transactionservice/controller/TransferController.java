package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.CreateTransferRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateNewTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreateTransferResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransferResponseDto;
import com.andersenlab.etalon.transactionservice.util.enums.ConfirmationMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface TransferController {
  String API_V1_URI = "/api/v1";
  String API_V2_URI = "/api/v2";
  String TRANSFERS_URI = "/transfers";
  String TRANSFER_ID_URI = "/{transferId}";
  String TRANSFER_CONFIRM_URI = "/confirmations";
  String METHOD_URI = "/{confirmationMethod}";
  String TRANSFER_CONFIRM_V2_URL =
      API_V2_URI + TRANSFERS_URI + TRANSFER_ID_URI + TRANSFER_CONFIRM_URI + METHOD_URI;
  String CONFIRMED_URL = "/confirmed";
  String TRANSFER_CONFIRMED_V2_URL = API_V2_URI + TRANSFERS_URI + TRANSFER_ID_URI + CONFIRMED_URL;
  String TRANSFERS_V1_URL = API_V1_URI + TRANSFERS_URI;
  String TRANSFERS_V2_URL = API_V2_URI + TRANSFERS_URI;
  String TRANSFERS_ID_URL = TRANSFERS_V1_URL + TRANSFER_ID_URI;
  String STATUS_URI = "/status";
  String TRANSFER_STATUS_UPDATE_URL = API_V1_URI + TRANSFERS_URI + TRANSFER_ID_URI + STATUS_URI;

  @Operation(summary = "Process creating transfer after confirmation v2 — US 7.0")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "202",
            description = "Transfer confirmation successful",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Transfer not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto processConfirmedTransfer(@PathVariable Long transferId);

  @Operation(summary = "Make a transfer v2 — US 7.0")
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
                            schema = @Schema(implementation = CreateTransferResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Transfer has been rejected",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  CreateNewTransferResponseDto createTransfer(
      @RequestParam(defaultValue = "false") boolean isTransient,
      @RequestBody @Valid CreateTransferRequestDto transferRequestDto);

  @Operation(summary = "Get transfer info by id — US 7.0")
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
                            schema = @Schema(implementation = TransferResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  TransferResponseDto getTransferById(@PathVariable final long transferId);

  @Operation(summary = "Create confirmation — US 7.0")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Transfer confirmation created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreateTransferResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Transfer not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  CreateTransferResponseDto createConfirmation(
      final long transferId, ConfirmationMethod confirmationMethod);

  @Operation(summary = "Delete transfer — US 7.0")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Transfer deleted"),
        @ApiResponse(
            responseCode = "404",
            description = "Transfer not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  void deleteTransfer(final long transferId);
}
