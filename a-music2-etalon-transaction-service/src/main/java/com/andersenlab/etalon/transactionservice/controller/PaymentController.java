package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.request.PaymentRequestDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.CreatePaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

public interface PaymentController {
  String API_V1_URI = "/api/v1";
  String PAYMENTS_URI = "/payments";
  String PAYMENT_ID_URI = "/{paymentId}";
  String PAYMENT_CONFIRM_URI = "/confirm";
  String PAYMENT_CONFIRM_URL = PAYMENT_ID_URI + PAYMENT_CONFIRM_URI;
  String PAYMENTS_URL = API_V1_URI + PAYMENTS_URI;
  String STATUS_URI = "/status";
  String PAYMENT_STATUS_UPDATE_URL = PAYMENT_ID_URI + STATUS_URI;

  @Operation(summary = "Make a payment — US 6.1")
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
                            schema = @Schema(implementation = CreatePaymentResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Payment has been rejected",
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
  CreatePaymentResponseDto createPayment(@Valid @RequestBody PaymentRequestDto dto);
}
