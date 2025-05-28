package com.andersenlab.etalon.transactionservice.controller;

import com.andersenlab.etalon.transactionservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.FinePaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.PaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.dto.transaction.response.TaxPaymentTypeResponseDto;
import com.andersenlab.etalon.transactionservice.util.filter.PaymentFilter;
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

public interface PaymentTypeController {
  String API_V1_URI = "/api/v1";
  String PAYMENT_TYPES_URI = "/payment-types";
  String PAYMENT_TYPE_ID_URI = "/{paymentTypeId}";
  String PAYMENT_TYPES_URL = API_V1_URI + PAYMENT_TYPES_URI;

  @Operation(summary = "Get payment types — US 6.0")
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
                                @Schema(
                                    oneOf = {
                                      TaxPaymentTypeResponseDto.class,
                                      PaymentTypeResponseDto.class,
                                      FinePaymentTypeResponseDto.class
                                    })))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<PaymentTypeResponseDto> getPaymentTypes(@ParameterObject PaymentFilter filter);

  @Operation(summary = "Get payment type by id — US 6.0")
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
                                @Schema(
                                    oneOf = {
                                      TaxPaymentTypeResponseDto.class,
                                      PaymentTypeResponseDto.class,
                                      FinePaymentTypeResponseDto.class
                                    })))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  PaymentTypeResponseDto getPaymentType(@PathVariable Long paymentTypeId);
}
