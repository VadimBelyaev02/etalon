package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CustomPageDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.EventResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionDetailedResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedPageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TransactionExtendedResponseDto;
import com.andersenlab.etalon.transactionservice.util.filter.TransactionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

public interface TransactionController {
  String API_V1_URI = "/api/v1";
  String API_V2_URI = "/api/v2";
  String TRANSACTIONS_URI = "/transactions";
  String ACCOUNTS_URI = "/accounts";
  String TRANSACTION_ID_PATH = "/{transactionId}";
  String TRANSACTIONS_V1_URL = API_V1_URI + TRANSACTIONS_URI;
  String TRANSACTIONS_V2_URL = API_V2_URI + TRANSACTIONS_URI;

  @Operation(summary = "Get all transactions — US 8.1")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = EventResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<EventResponseDto> getAllTransactions(@ParameterObject TransactionFilter filter);

  @Operation(summary = "Get detailed transaction  — US 8.1")
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
                                @Schema(implementation = TransactionDetailedResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  TransactionDetailedResponseDto getDetailedTransaction(@PathVariable Long transactionId);

  @Operation(summary = "Get all transactions with events — US 8.1")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "List of transactions with pagination",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TransactionExtendedPageResponseDto.class))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  CustomPageDto<TransactionExtendedResponseDto> getAllExtendedTransactions(
      @ParameterObject TransactionFilter filter);
}
