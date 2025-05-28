package com.andersenlab.etalon.infoservice.controller;

import com.andersenlab.etalon.infoservice.dto.request.ConfirmationRequestDto;
import com.andersenlab.etalon.infoservice.dto.response.CreateConfirmationResponseDto;
import com.andersenlab.etalon.infoservice.dto.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ConfirmationController {
  String API_V1_URI = "/api/v1";
  String CONFIRMATIONS_URI = "/confirmations";
  String CONFIRMATION_ID_URI = "/{confirmationId}";
  String CONFIRMATION_EMAIL = "/email";
  String CONFIRMATION_RESEND = "/resend";
  String CONFIRMATIONS_URL = API_V1_URI + CONFIRMATIONS_URI;
  String CONFIRMATION_BY_CONFIRMATION_ID_AND_EMAIL = CONFIRMATION_ID_URI + CONFIRMATION_EMAIL;
  String CONFIRMATION_RESEND_BY_ID_URL = CONFIRMATION_ID_URI + CONFIRMATION_RESEND;
  String CONFIRMATION_STATUS = "/status";

  @Operation(summary = "Verify confirmation — RR-US 1.1")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Confirmation verified",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(schema = @Schema(implementation = MessageResponseDto.class)))),
        @ApiResponse(
            responseCode = "400",
            description = "Confirmation code cannot be invalid, either expired or blocked",
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
            description = "Confirmation request with id %s is not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto verifyConfirmation(
      @PathVariable Long confirmationId, @RequestBody ConfirmationRequestDto dto);

  @Operation(summary = "Resend confirmation code  — RR-US 1.1")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "New confirmation code has been sent to your email",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema =
                                @Schema(implementation = CreateConfirmationResponseDto.class)))),
        @ApiResponse(
            responseCode = "401",
            description = "User is not authorized",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Confirmation with id %s is not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  CreateConfirmationResponseDto resendConfirmationCode(@PathVariable final Long confirmationId);
}
