package com.andersenlab.etalon.infoservice.controller;

import com.andersenlab.etalon.infoservice.dto.response.CountryCodesResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;

public interface CountryCodesController {
  String API_V1_URI = "/api/v1";
  String INFO_URI = "/info";
  String COUNTRY_CODES_URI = "/country-codes";

  String COUNTRY_CODES_INFO_URL = API_V1_URI + INFO_URI;

  @Operation(summary = "Get all country phone codes — US 7.2.1")
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
                            schema = @Schema(implementation = CountryCodesResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "This locale is not supported",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  List<CountryCodesResponseDto> getAllCountryCodes(
      @RequestParam(required = false, defaultValue = "en") String locale);
}
