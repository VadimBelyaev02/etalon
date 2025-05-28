package com.andersenlab.etalon.userservice.controller;

import com.andersenlab.etalon.userservice.dto.common.response.MessageResponseDto;
import com.andersenlab.etalon.userservice.dto.modification.request.UserEmailModificationRequestDto;
import com.andersenlab.etalon.userservice.dto.modification.responce.EmailModificationResponseDto;
import com.andersenlab.etalon.userservice.dto.reset.confirmation.ResetPasswordConfirmationRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.request.ResetPasswordRequestDto;
import com.andersenlab.etalon.userservice.dto.reset.response.ResetPasswordResponseDto;
import com.andersenlab.etalon.userservice.dto.user.request.UserPatchRequestDto;
import com.andersenlab.etalon.userservice.dto.user.response.UserDataResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserDataController {

  String API_V1_URI = "/api/v1";
  String USERS_URI = "/users";
  String DETAILS_URI = "/details";
  String PHONE_URI = "/phone";
  String USER_ID_PATH = "/{userId}";
  String PHONE_NUMBER_PATH = "/{phoneNumber}";
  String USER_DETAILS_BY_PHONE_NUMBER_URL = PHONE_URI + PHONE_NUMBER_PATH + DETAILS_URI;
  String USERS_URL = API_V1_URI + USERS_URI;
  String MODIFICATION_URI = "/modification";
  String EMAIL_URI = "/email";
  String CONFIRMATION_URI = "/confirmation";
  String USER_EMAIL_MODIFICATION = MODIFICATION_URI + EMAIL_URI;
  String USER_EMAIL_MODIFY_CONFIRMATION = MODIFICATION_URI + EMAIL_URI + CONFIRMATION_URI;
  String RESET_PASSWORD_URI = "/reset-password";
  String LINK_URI = "/link";
  String CONFIRM_URI = "/confirm";
  String RESET_PASSWORD_LINK_URL = RESET_PASSWORD_URI + LINK_URI;
  String RESET_PASSWORD_CONFIRM_URL = RESET_PASSWORD_URI + CONFIRM_URI;

  @Operation(summary = "Get authenticated user details")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDataResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "User with such credentials (id) does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  UserDataResponseDto getUserData(final String userId);

  @Operation(summary = "Change user details")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User with id: (id) updated",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "User with such credentials (id) does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto updateUserData(@RequestBody @Valid final UserPatchRequestDto userRequestDto);

  @Operation(summary = "Get authenticated user by phone number")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDataResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "User with such credentials (id) does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  UserDataResponseDto getUserDataByPhoneNumber(final String phoneNumber);

  @Operation(summary = "Request user email modification")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "User email modification request sent",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "User with such credentials (id) does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  EmailModificationResponseDto requestEmailModification(
      @RequestBody @Valid final UserEmailModificationRequestDto userEmailModificationRequestDto);

  @Operation(summary = "Request password reset link")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password reset link sent successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResetPasswordResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "User with provided email does not exist",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  ResetPasswordResponseDto requestResetPasswordLink(
      @RequestBody @Valid final ResetPasswordRequestDto resetPasswordRequestDto);

  @Operation(summary = "Confirm password reset")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Password has been reset successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid token or passwords don't match",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto confirmResetPassword(
      @RequestBody @Valid
          final ResetPasswordConfirmationRequestDto resetPasswordConfirmationRequestDto);
}
