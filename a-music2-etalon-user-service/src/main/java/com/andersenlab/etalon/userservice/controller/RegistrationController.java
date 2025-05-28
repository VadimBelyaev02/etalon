package com.andersenlab.etalon.userservice.controller;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.CompleteRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.request.InitiateRegistrationRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.InitiateRegistrationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.MediaType;

public interface RegistrationController {

  String API_V1_URI = "/api/v1";
  String REGISTRATIONS_URI = "/registrations";
  String REGISTRATIONS_URL = API_V1_URI + REGISTRATIONS_URI;
  String COGNITO = "/cognito";
  String CONFIRM = "/confirm";
  String COMPLETE_REGISTRATION_ID = "/cognito/{registrationId}";
  String REGISTRATIONS_URL_INITIATE = REGISTRATIONS_URL + COGNITO;
  String REGISTRATIONS_URL_CONFIRM = REGISTRATIONS_URL + CONFIRM;

  @Operation(summary = "Initiate user registration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registration initiated successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = InitiateRegistrationResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "PESEL cannot be blank or null | Invalid PESEL format",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Client with provided PESEL not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description =
                "Registration already initiated for this PESEL | User with such PESEL already registered",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  InitiateRegistrationResponseDto initiateRegistration(final InitiateRegistrationRequestDto dto);

  @Operation(summary = "Complete user registration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registration completed successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Cognito User ID cannot be blank or null | Registration is not confirmed",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Registration with ID {registrationId} not found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = "User with this Cognito User ID already exists",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto completeRegistration(
      final UUID registrationId, final CompleteRegistrationRequestDto dto);
}
