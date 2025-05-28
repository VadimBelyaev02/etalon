package com.andersenlab.etalon.depositservice.controller;

import com.andersenlab.etalon.depositservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.depositservice.dto.deposit.response.DepositProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.MediaType;

public interface DepositProductController {
  String API_V1_URI = "/api/v1";
  String PRODUCTS_URI = "/products";
  String DEPOSITS_URI = "/deposits";
  String PRODUCTS_URL = API_V1_URI + PRODUCTS_URI + DEPOSITS_URI;

  @Operation(summary = "Get all deposit products")
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
                            schema = @Schema(implementation = DepositProductResponseDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find deposit product with id %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
      })
  List<DepositProductResponseDto> getAllDepositProducts();
}
