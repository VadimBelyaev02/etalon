package com.andersenlab.etalon.infoservice.controller;

import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import com.andersenlab.etalon.infoservice.util.filter.TransactionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface DocumentController {

  String API_V1_URI = "/api/v1";
  String INFO_URI = "/info";
  String RECEIPTS_URI = "/receipts";
  String DOCUMENTS_URI = "/documents";
  String STATEMENTS_URI = "/statements";
  String CONFIRMATION_URI = "/confirmation";
  String TRANSACTIONS_URI = "/transactions";
  String TRANSFERS_URI = "/transfers";
  String CONFIRMATION_STATEMENT_URI = STATEMENTS_URI + CONFIRMATION_URI;
  String BANK_INFO_DOCUMENT_URL = API_V1_URI + INFO_URI + DOCUMENTS_URI;
  String TRANSACTIONS_STATEMENT_URI = STATEMENTS_URI + TRANSACTIONS_URI;
  String TRANSFERS_RECEIPTS_URI = RECEIPTS_URI + TRANSFERS_URI;
  String DEFAULT_FILE_NAME = "file";

  @Operation(summary = "Download transaction confirmation pdf — US 8.5")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Document was downloaded",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description =
                "User with such credentials (userId) does not exist | Detailed transaction view has been rejected because given accounts in this transaction doesn't belong to this user",
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
  ResponseEntity<Resource> downloadConfirmationPdf(
      @RequestParam Long transactionId,
      @RequestParam(required = false, defaultValue = "en") String locale);

  @Operation(summary = "Download transactions statement — US 8.5")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transactions statement was downloaded",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description =
                "User with such credentials (userId) does not exist | View transactions for account has been rejected because given account number does not belong to this user",
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
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = "This statement type is not supported",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  ResponseEntity<Resource> downloadTransactionsStatement(
      @RequestParam(defaultValue = "pdf") String statementType,
      @ParameterObject TransactionFilter filter,
      @RequestParam(required = false, defaultValue = "en") String locale);

  @Operation(summary = "Download transfer receipt — US 7.1.1")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transfer receipt was downloaded",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
        @ApiResponse(
            responseCode = "400",
            description =
                "User with such credentials (userId) does not exist | View transactions for account has been rejected because given account number does not belong to this user",
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
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  ResponseEntity<Resource> downloadReceiptPdf(@RequestParam Long transferId);

  @GetMapping("terms-and-policy")
  ResponseEntity<Resource> downloadTermsAndPolicy();
}
