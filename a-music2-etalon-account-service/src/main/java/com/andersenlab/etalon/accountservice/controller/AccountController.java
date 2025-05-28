package com.andersenlab.etalon.accountservice.controller;

import com.andersenlab.etalon.accountservice.dto.account.request.AccountCreationRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.AccountRequestDto;
import com.andersenlab.etalon.accountservice.dto.account.request.RequestOptionDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountInfoResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountNumberResponseDto;
import com.andersenlab.etalon.accountservice.dto.account.response.AccountResponseDto;
import com.andersenlab.etalon.accountservice.dto.common.response.MessageResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;

public interface AccountController {
  String API_V1_URI = "/api/v1";
  String API_V2_URI = "/api/v2";
  String ACCOUNTS_URI = "/accounts";
  String REPLENISHED_URI = "/replenished";
  String WITHDRAWN_URI = "/withdrawn";
  String ACCOUNT_ID_PATH = "/{accountId}";
  String ACCOUNT_NUMBER_PATH = "/{accountNumber}";
  String ACCOUNT_V1_URL = API_V1_URI + ACCOUNTS_URI;
  String ACCOUNT_V2_URL = API_V2_URI + ACCOUNTS_URI;
  String ACCOUNT_V1_ID_PATH_URL = ACCOUNT_V1_URL + ACCOUNT_ID_PATH;

  @Operation(summary = "Get account info by selected option")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account information provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountInfoResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  AccountInfoResponseDto getAllAccountInfoBySelectedOption(
      @ParameterObject @Valid RequestOptionDto options);

  @Operation(summary = "Get all accounts by userId")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "All user accounts provided",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array =
                        @ArraySchema(
                            schema = @Schema(implementation = AccountNumberResponseDto.class)))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find any account",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  List<AccountNumberResponseDto> getAllAccountNumbers(String userId);

  @Operation(summary = "Update account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account updated successfully",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class))),
        @ApiResponse(
            responseCode = "404",
            description = "Cannot find account entity with account number %s",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponseDto.class)))
      })
  MessageResponseDto updateAccount(Long accountId, final AccountRequestDto accountRequestDto);

  @Operation(summary = "Add new account")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Added new account",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountResponseDto.class)))
      })
  AccountResponseDto addNewAccount(AccountCreationRequestDto accountCreation);
}
