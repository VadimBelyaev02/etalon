package com.andersenlab.etalon.loanservice.controller;

import com.andersenlab.etalon.loanservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.loanservice.dto.loan.response.LoanProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.MediaType;

public interface LoanProductController {
  String API_V1_URI = "/api/v1";
  String PRODUCTS_URI = "/products";
  String LOANS_URI = "/loans";
  String PRODUCTS_URL = API_V1_URI + PRODUCTS_URI + LOANS_URI;

  @Operation(summary = "Get all loan products — US 4.12")
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
                            schema = @Schema(implementation = LoanProductResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<LoanProductResponseDto> getAllLoanProducts();
}
