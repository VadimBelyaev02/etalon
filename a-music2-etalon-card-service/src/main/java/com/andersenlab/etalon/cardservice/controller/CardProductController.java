package com.andersenlab.etalon.cardservice.controller;

import com.andersenlab.etalon.cardservice.dto.card.response.CardProductResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.MediaType;

public interface CardProductController {
  String API_V1_URI = "/api/v1";
  String PRODUCTS_URI = "/products";
  String CARDS_URI = "/cards";
  String CARD_PRODUCTS_URL = API_V1_URI + PRODUCTS_URI + CARDS_URI;

  @Operation(summary = "Get all card products")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "All card products provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = CardProductResponseDto.class))))
      })
  List<CardProductResponseDto> getAllCardProducts();
}
