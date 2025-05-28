package com.andersenlab.etalon.loanservice.controller;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.request.LoanOrderRequestDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderDetailedResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

public interface LoanOrderController {

  String API_V1_URI = "/api/v1";
  String ORDERS_URI = "/orders";
  String LOANS_URI = "/loans";
  String ORDER_ID_PATH = "/{orderId}";
  String LOAN_ORDERS_URL = API_V1_URI + ORDERS_URI + LOANS_URI;

  @Operation(summary = "Get all loan orders — US 4.3")
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
                            schema = @Schema(implementation = LoanOrderResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<LoanOrderResponseDto> getAllLoanOrders();

  @Operation(summary = "Get loan order details — US 4.6")
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
                                @Schema(implementation = LoanOrderDetailedResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find loan order with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  LoanOrderDetailedResponseDto getLoanOrderDetailed(@PathVariable("orderId") Long orderId);

  @Operation(summary = "Create loan order — US 4.7")
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
            responseCode = "400",
            description =
                "The amount goes outside of the selected loan product | Incorrect loan duration | Your "
                    + "salary is less than your monthly expenses | Guarantor with matching PESEL but different "
                    + "name/surname already exists | The count of guarantors specified does not match the count "
                    + "of required guarantors",
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
            responseCode = "404",
            description = "Cannot find loan product with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto createLoanOrder(@Valid final LoanOrderRequestDto loanOrderRequestDto);
}
