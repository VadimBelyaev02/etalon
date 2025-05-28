package com.andersenlab.etalon.userservice.controller;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.info.response.StatusMessageResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.EmailDto;
import com.andersenlab.etalon.userservice.dto.user.request.PeselDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

public interface ValidatorController {
  String API_V1_URI = "/api/v1";
  String USERS_EMAILS_URI = "/user-emails";
  String VALIDATED_URI = "/validated";
  String PESELS_URI = "/user-pesels";
  String VALIDATED_EMAIL_BEFORE_REGISTRATION_URI = USERS_EMAILS_URI + VALIDATED_URI;
  String VALIDATED_PESEL_BEFORE_REGISTRATION_URI = PESELS_URI + VALIDATED_URI;
  String VALIDATED_EMAIL_BEFORE_REGISTRATION_URL = API_V1_URI + USERS_EMAILS_URI + VALIDATED_URI;
  String VALIDATED_PESEL_BEFORE_REGISTRATION_URL = API_V1_URI + PESELS_URI + VALIDATED_URI;

  @Operation(summary = "Validate email before registration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = StatusMessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Email is not available to register",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  StatusMessageResponseDto validateEmailBeforeRegistration(final EmailDto emailDto);

  @Operation(summary = "Validate pesel before registration")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = StatusMessageResponseDto.class))),
        @ApiResponse(
            responseCode = "409",
            description = "Pesel is not available to register",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  StatusMessageResponseDto validatePeselBeforeRegistration(final PeselDto dto);
}
