package com.andersenlab.etalon.infoservice.controller;

import com.andersenlab.etalon.infoservice.dto.request.BaseEmailRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailController {

  String API_V1_URI = "/api/v1";

  String SEND_EMAIL = "/send-email";

  @Operation(summary = "Send email to the user — US 9.1.4")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email has been sent to the user",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  void sendEmail(@RequestBody BaseEmailRequestDto dto);
}
